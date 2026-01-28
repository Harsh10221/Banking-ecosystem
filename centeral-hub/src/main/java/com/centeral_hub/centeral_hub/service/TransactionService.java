package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.model.LegderModel;
import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.repository.LedgerRepository;
import com.centeral_hub.centeral_hub.model.SettlementLogsModel;
import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.repository.LedgerRepository;
import com.centeral_hub.centeral_hub.repository.SettlementRepository;
import com.centeral_hub.centeral_hub.repository.TransactionRepository;
import com.centeral_hub.centeral_hub.utils.KafkaMonitorService;
import com.centeral_hub.centeral_hub.utils.ResponseObject;
import com.centeral_hub.centeral_hub.utils.TransactionCheck;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service

public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    //    for every bank there will be different secret, the bank will send there token and there bank code "Bob"
//    then we will first try to decode the token if it works then we will use that as the sender bank name.
    @Value("${next_gen_bank_secret}")
    private String secretKeyOfNextBank;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestClient restClient;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SettlementRepository settlementRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private TransactionCheck transactionCheck;

    @Autowired
    private KafkaMonitorService kafkaMonitorService;

    private Long existingTransactionId;

    UUID uuid = UUID.randomUUID();


    /// There is no sense of returning a ResponseEntity
    public ResponseEntity<?> processInboundTransfer(String senderAccountNo, String senderBank, BigDecimal amount, String type, String receiverAccountNo, String receiverBank, String bankToken, String userRequestKey) {


        if (senderAccountNo == null || amount.compareTo(BigDecimal.ZERO) <= 0 || !type.equals("Debit") || receiverAccountNo == null || receiverBank == null || bankToken == null || userRequestKey == null) {
            /// Sender bank name was removed

            JsonNode response = restClient.post()
                    .uri("/api/transaction/webhook/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("Fields required", "Failed,field error"))
                    .retrieve()
                    .body(JsonNode.class);


            return ResponseEntity.badRequest().body("Error: Fields are required");
        }


        TransactionCheck.TransactionStatus status = transactionCheck.checkAndProcess(userRequestKey);



        if (status == TransactionCheck.TransactionStatus.ALREADY_PROCESSING) {
            return ResponseEntity.badRequest().body("Redis, Transaction is already in process");
        }

        // Resolve Bank URLs
        String receiverBankUrl = resolveBankUrl(transactionModel.getReceiverBank());
        String senderBankUrl = resolveBankUrl(transactionModel.getSenderBank());

        transactionModel.setSenderBank(bankToken);
        transactionModel.setReceiverBank(receiverBank);
        transactionModel.setReceiverAccountNumber(receiverAccountNo);
        transactionModel.setAmount(amount);
        transactionModel.setSenderAccountNumber(senderAccountNo);
        transactionModel.setCorrelationId(uuid);

        try {
            TransactionModel savedTransaction = transactionRepository.save(transactionModel);
            existingTransactionId = savedTransaction.getTransactionId();

            UUID correlationId = savedTransaction.getCorrelationId();

            if (correlationId == null) {

                var requestBody = Map.of(
                        "AccountNo", senderAccountNo,
                        "amount", amount,
                        "receiverAccountNumber", receiverAccountNo,
                        "receiverBank", receiverBank,
                        "Error", "CorrelationId error, id not generated"
                );

                ResponseObject.createResponseObj(400, "Failed correlationId", null);

                JsonNode response = restClient.post()
                        .uri("/api/transaction/webhook/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(JsonNode.class);
                System.out.println("response " + response);

                savedTransaction.setStatus(TransactionModel.Status.FAILED);
                savedTransaction.setErrorMsg("Correlation ID is missing");

                transactionRepository.save(savedTransaction);

                return ResponseEntity.internalServerError().body("Transaction saved, but correlation Id is missing");
            }


            JsonNode accountValidateResponse = restClient.post()
                    .uri("/account/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("accountNo", receiverAccountNo))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, ((request, response) -> {
                    }))
                    .body(JsonNode.class);

            String message = accountValidateResponse.get("message").asText();
            int statusCode = accountValidateResponse.get("statusCode").asInt();

            SettlementLogsModel settlementLogsModel = new SettlementLogsModel();

            if (!message.equalsIgnoreCase("Success") || statusCode != 200) {
                transactionTemplate.execute(status1 -> {

                    savedTransaction.setStatus(TransactionModel.Status.FAILED);
                    savedTransaction.setErrorMsg(message);

                    settlementLogsModel.setCorrelationId(correlationId);
                    settlementLogsModel.setBankServiceName(bankToken);
                    settlementLogsModel.setDirection(SettlementLogsModel.Direction.OUTBOUND);
                    settlementLogsModel.setResponseCode(String.valueOf(statusCode));
                    settlementLogsModel.setRawPayload(accountValidateResponse.toString());
                    settlementLogsModel.setRetryCount(0);

                    settlementRepository.save(settlementLogsModel);
                    return null;
                });
                JsonNode response = restClient.post()
                        .uri("/api/transaction/webhook/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("Account validation", "Failed, error"))
                        .retrieve()
                        .body(JsonNode.class);

                System.out.println("Account validation " + response);

                return ResponseEntity.badRequest().body("Account validation failed");

            }

            savedTransaction.setStatus(TransactionModel.Status.VALIDATED);
            transactionRepository.save(savedTransaction);

            //withdraw
            var withdrawDataBody = Map.of(
                    "accountNumber", senderAccountNo,
                    "type", "Withdraw",
                    "amount", amount
            );


            JsonNode withdrawInitiateRequest = restClient.post()
                    .uri("/api/transaction/withdraw")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(withdrawDataBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (((request, response) -> {
                    })))
                    .body(JsonNode.class);


            String withdrawStatus = withdrawInitiateRequest.get("statusCode").asText();
            String withdrawMsg = withdrawInitiateRequest.get("message").asText();
            String withdrawResponseMsg = withdrawInitiateRequest.toString();

            SettlementLogsModel settlementLogsModel1 = new SettlementLogsModel();

            if (!withdrawInitiateRequest.get("statusCode").asText().equals("200")) {
                transactionTemplate.execute(status1 -> {
                    savedTransaction.setStatus(TransactionModel.Status.FAILED);
                    savedTransaction.setErrorMsg(withdrawMsg);

                    settlementLogsModel1.setCorrelationId(correlationId);
                    settlementLogsModel1.setBankServiceName(receiverBank);
                    settlementLogsModel1.setDirection(SettlementLogsModel.Direction.OUTBOUND);
                    settlementLogsModel1.setResponseCode(withdrawStatus);
                    settlementLogsModel1.setRawPayload(withdrawResponseMsg);
                    settlementLogsModel1.setRetryCount(0);

                    settlementRepository.save(settlementLogsModel1);
                    return null;

                });

                var requestBody = Map.of(
                        "Message", "Transaction Failed",
                        "senderAccountNumber", senderAccountNo,
                        "senderBank", bankToken,
                        "receiverAccountNumber", receiverAccountNo,
                        "receiverBank", receiverBank,
                        "amount", amount,
                        "Status", "False",
                        "Error",withdrawMsg
                );

                JsonNode response = restClient.post()
                        .uri("/api/transaction/webhook/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(JsonNode.class);

                return ResponseEntity.internalServerError().body("There was an error in the withdraw");
            }
            transactionTemplate.execute(status1 -> {

                settlementLogsModel1.setCorrelationId(correlationId);
                settlementLogsModel1.setBankServiceName(receiverBank);
                settlementLogsModel1.setDirection(SettlementLogsModel.Direction.OUTBOUND);
                settlementLogsModel1.setResponseCode(withdrawStatus);
                settlementLogsModel1.setRawPayload(withdrawResponseMsg);
                settlementLogsModel1.setRetryCount(0);

                savedTransaction.setStatus(TransactionModel.Status.PENDING);

                settlementRepository.save(settlementLogsModel1);

                return null;
            });

            var depositDataBody = Map.of(
                    "accountNumber", receiverAccountNo,
                    "type", "Deposit",
                    "amount", amount
            );

            JsonNode depositInitiateRequest = restClient.post()
                    .uri("/api/transaction/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(refundData)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (((request, response) -> {
                    })))
                    .body(JsonNode.class);

            String depositStatus = depositInitiateRequest.get("statusCode").asText();
            String depositResponseMsg = depositInitiateRequest.toString();


            SettlementLogsModel settlementLogsModel2 = new SettlementLogsModel();
            if (!depositStatus.equals("200")) {
                transactionTemplate.execute(status1 -> {
                    savedTransaction.setStatus(TransactionModel.Status.FAILED);
                    savedTransaction.setErrorMsg(depositResponseMsg);

                    settlementLogsModel2.setCorrelationId(correlationId);
                    settlementLogsModel2.setBankServiceName(senderBank);
                    settlementLogsModel2.setDirection(SettlementLogsModel.Direction.OUTBOUND);
                    settlementLogsModel2.setResponseCode(depositStatus);
                    settlementLogsModel2.setRawPayload(depositResponseMsg);
                    settlementLogsModel2.setRetryCount(0);

                    settlementRepository.save(settlementLogsModel2);

                    return null;
                });
                return ResponseEntity.internalServerError().body("There was an error in the Deposit in receiver bank");

            }

            transactionTemplate.execute(status1 -> {

                settlementLogsModel2.setCorrelationId(correlationId);
                settlementLogsModel2.setBankServiceName(bankToken);
                settlementLogsModel2.setDirection(SettlementLogsModel.Direction.OUTBOUND);
                settlementLogsModel2.setResponseCode(depositStatus);
                settlementLogsModel2.setRawPayload(depositResponseMsg);
                settlementLogsModel2.setRetryCount(0);

                savedTransaction.setStatus(TransactionModel.Status.SUCCESS);
                settlementRepository.save(settlementLogsModel2);

                LegderModel debit = new LegderModel();
                debit.setCorrelationId(correlationId);
                debit.setTransactionId(String.valueOf(savedTransaction.getTransactionId()));
                debit.setTransactionType(LegderModel.Transactiontype.DEBIT);
                debit.setAmount(amount);
                debit.setBank(bankToken);
                debit.setDescription("Transfer to " + receiverBank + " bank");
                ledgerRepository.save(debit);

                LegderModel credit = new LegderModel();
                credit.setCorrelationId(correlationId);
                credit.setTransactionId(String.valueOf(savedTransaction.getTransactionId()));
                credit.setTransactionType(LegderModel.Transactiontype.CREDIT);
                credit.setAmount(amount);
                credit.setBank(receiverBank);
                credit.setDescription("Received from " + senderBank + " bank");

                ledgerRepository.save(credit);

                return null;
            });

            var requestBody = Map.of(
                    "Message", "Transaction success",
                    "senderAccountNumber", senderAccountNo,
                    "senderBank", bankToken,
                    "receiverAccountNumber", receiverAccountNo,
                    "receiverBank", receiverBank,
                    "amount", amount,
                    "Status", "True"
            );

            JsonNode response = restClient.post()
                    .uri("/api/transaction/webhook/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);



            return ResponseEntity.ok("Success");



        } catch (Exception e) {
            transactionRepository.changeStatusAndAddErrorMsg(existingTransactionId, TransactionModel.Status.FAILED, e.getMessage());

            var requestBody = Map.of(
                    "Message", "Transaction Failed",
                    "senderAccountNumber", senderAccountNo,
                    "senderBank", bankToken,
                    "receiverAccountNumber", receiverAccountNo,
                    "receiverBank", receiverBank,
                    "amount", amount,
                    "Status", "False",
                    "Error", e.getMessage()
            );

            JsonNode response = restClient.post()
                    .uri("/api/transaction/webhook/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);


            return ResponseEntity.badRequest().body("bad") ;
//            throw new RuntimeException("Error in catch block ", e);

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