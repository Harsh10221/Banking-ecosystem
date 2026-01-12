package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.model.LegderModel;
import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.repository.LedgerRepository;
import com.centeral_hub.centeral_hub.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final LedgerRepository ledgerRepository;
    private final RestClient restClient;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, 
                              LedgerRepository ledgerRepository, 
                              RestClient restClient) {
        this.transactionRepository = transactionRepository;
        this.ledgerRepository = ledgerRepository;
        this.restClient = restClient;
    }

    public String processInboundTransfer(TransactionModel transactionModel, String token) {
        
        // 1. Initialize Transaction Status
        transactionModel.setCorrelationId(UUID.randomUUID());
        transactionModel.setStatus(TransactionModel.Status.INITIATED);
        transactionModel.setCreatedAt(LocalDateTime.now());
        transactionModel.setUpdatedAt(LocalDateTime.now());
        
        transactionRepository.save(transactionModel);

        String receiverAccountNo = transactionModel.getReceiverAccountNumber();
        String senderAccountNo = transactionModel.getSenderAccountNumber();
        BigDecimal amount = transactionModel.getAmount();

        // Resolve Bank URLs
        String receiverBankUrl = resolveBankUrl(transactionModel.getReceiverBank());
        String senderBankUrl = resolveBankUrl(transactionModel.getSenderBank());

        logger.info("Starting Transaction: {} | Amount: {}", transactionModel.getCorrelationId(), amount);

        try {
            // =================================================================
            // STEP 1: VALIDATE RECEIVER
            // =================================================================
            try {
                // Note: The URL is constructed using the resolved Bank URL
                String validationResponse = restClient.post()
                        .uri(receiverBankUrl + "/account/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("accountNo", receiverAccountNo))
                        .retrieve()
                        .body(String.class);

                if (!"Success".equals(validationResponse)) {
                    logger.warn("Validation failed for receiver: {}", receiverAccountNo);
                    updateTransactionStatus(transactionModel, TransactionModel.Status.FAILED);
                    return "Transaction Failed: Receiver Account Invalid";
                }
            } catch (Exception e) {
                logger.error("Network error validating receiver", e);
                updateTransactionStatus(transactionModel, TransactionModel.Status.FAILED);
                return "Transaction Failed: Receiver Bank Unreachable";
            }

            // =================================================================
            // STEP 2: WITHDRAW FROM SENDER (The Point of No Return)
            // =================================================================
            Map<String, Object> withdrawData = new HashMap<>();
            withdrawData.put("accountNumber", senderAccountNo);
            withdrawData.put("amount", amount);
            withdrawData.put("type", "Withdraw");

            JsonNode withdrawResponse;
            try {
                withdrawResponse = restClient.post()
                        .uri(senderBankUrl + "/api/transaction/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(withdrawData)
                        .retrieve()
                        .body(JsonNode.class);
            } catch (Exception e) {
                logger.error("Sender withdrawal failed", e);
                updateTransactionStatus(transactionModel, TransactionModel.Status.FAILED);
                return "Transaction Failed: Sender Bank Unreachable";
            }

            if (withdrawResponse == null || withdrawResponse.get("statusCode").asInt() != 200) {
                logger.warn("Sender withdrawal rejected: {}", withdrawResponse);
                updateTransactionStatus(transactionModel, TransactionModel.Status.FAILED);
                return "Transaction Failed: Sender Withdrawal Declined";
            }

            // =================================================================
            // STEP 3: DEPOSIT TO RECEIVER (Critical Phase)
            // =================================================================
            Map<String, Object> depositData = new HashMap<>();
            depositData.put("accountNumber", receiverAccountNo);
            depositData.put("amount", amount);
            depositData.put("type", "Credit");

            boolean depositSuccess = false;
            try {
                JsonNode depositResponse = restClient.post()
                        .uri(receiverBankUrl + "/api/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(depositData)
                        .retrieve()
                        .body(JsonNode.class);

                if (depositResponse != null && depositResponse.get("statusCode").asInt() == 200) {
                    depositSuccess = true;
                }
            } catch (Exception e) {
                logger.error("Deposit request failed", e);
                depositSuccess = false;
            }

            if (depositSuccess) {
                // Both succeeded
                logger.info("Transaction Completed: {}", transactionModel.getCorrelationId());
                updateTransactionStatus(transactionModel, TransactionModel.Status.COMPLETED);

                // Create Ledger Entry
                LegderModel ledgerEntry = new LegderModel();
                ledgerEntry.setTransactionId(transactionModel.getTransactionId());
                ledgerEntry.setCorrelationId(transactionModel.getCorrelationId());
                ledgerEntry.setAmount(amount);
                ledgerEntry.setSenderBank(transactionModel.getSenderBank());
                ledgerEntry.setReceiverBank(transactionModel.getReceiverBank());
                ledgerEntry.setTransactiontype(LegderModel.Transactiontype.DEBIT);
                ledgerEntry.setCreatedAt(LocalDateTime.now());
                
                ledgerRepository.save(ledgerEntry);

                return "Transfer Successful";
            } else {
                // =================================================================
                // CRITICAL: SAGA COMPENSATION (ROLLBACK)
                // =================================================================
                logger.error("CRITICAL: Deposit failed. Initiating REFUND for Sender: {}", senderAccountNo);
                
                boolean refundSuccess = performCompensatingTransaction(senderBankUrl, senderAccountNo, amount);
                
                if (refundSuccess) {
                    updateTransactionStatus(transactionModel, TransactionModel.Status.FAILED);
                    return "Transaction Failed: Receiver Deposit Failed (Funds have been refunded)";
                } else {
                    // Alert Admin: Money is stuck!
                    updateTransactionStatus(transactionModel, TransactionModel.Status.REVERSED); 
                    return "Transaction Failed: CRITICAL ERROR - Funds deducted but not refunded. Contact Support.";
                }
            }

        } catch (Exception e) {
            logger.error("Unexpected system error", e);
            updateTransactionStatus(transactionModel, TransactionModel.Status.FAILED);
            return "Transaction Failed: " + e.getMessage();
        }
    }

    /**
     * SAGA PATTERN: Compensating Transaction
     * Reverses the withdrawal if the deposit fails.
     */
    private boolean performCompensatingTransaction(String bankUrl, String accountNumber, BigDecimal amount) {
        try {
            Map<String, Object> refundData = new HashMap<>();
            refundData.put("accountNumber", accountNumber);
            refundData.put("amount", amount);
            refundData.put("type", "Credit"); // Refund = Credit back the money

            JsonNode refundResponse = restClient.post()
                    .uri(bankUrl + "/api/transaction/deposit") // Uses deposit endpoint to refund
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(refundData)
                    .retrieve()
                    .body(JsonNode.class);

            if (refundResponse != null && refundResponse.get("statusCode").asInt() == 200) {
                logger.info("REFUND SUCCESSFUL for Account: {}", accountNumber);
                return true;
            } else {
                logger.error("REFUND REJECTED by Bank. Response: {}", refundResponse);
                return false;
            }
        } catch (Exception e) {
            logger.error("REFUND NETWORK ERROR. Manual Reconciliation Required for Account: " + accountNumber, e);
            return false;
        }
    }

    private void updateTransactionStatus(TransactionModel transaction, TransactionModel.Status status) {
        transaction.setStatus(status);
        transaction.setUpdatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
    
    // Helper to resolve Bank URLs based on Bank Name/ID
    private String resolveBankUrl(String bankName) {
        // NOTE: In production, this should query a Service Registry (Eureka) or Database.
        // For now, if your base URL in RestConfig is "http://localhost:8080", 
        // returning an empty string "" will use that base URL.
        // Or you can return specific URLs for different banks.
        return ""; 
    }
}