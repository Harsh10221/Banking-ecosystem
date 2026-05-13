package com.banking.net_banking_system.service;

import com.banking.net_banking_system.dtos.*;
import com.banking.net_banking_system.model.TransactionModel;
import com.banking.net_banking_system.model.TransferModel;
import com.banking.net_banking_system.model.UserModel;
import com.banking.net_banking_system.repository.AccountRepository;
import com.banking.net_banking_system.repository.TransactionRepository;
import com.banking.net_banking_system.repository.TransferRepository;

import com.banking.net_banking_system.utils.GetPrivateKey;
import com.banking.net_banking_system.utils.IncomingRequestDto;
import com.banking.net_banking_system.utils.ResponseDto;
import com.banking.net_banking_system.utils.ResponseObj;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.security.PrivateKey;
import java.util.UUID;


@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestClient restClient;

    @Autowired
    private PrivateKey privateKey;





//        2026 - 04 - 30 T00:
//        12:50.674 + 05:30 ERROR 10496-- - [nio-8080-exec-4]o.a.c.c.C.[.[.[/].[dispatcherServlet]    :
//        Servlet.service() for servlet[dispatcherServlet] in context with path[] threw exception[ Request processing
//        failed:
//        org.springframework.transaction.UnexpectedRollbackException:Transaction silently rolled back because it has been
//        marked as rollback - only]with root cause
//
//        org.springframework.transaction.UnexpectedRollbackException:Transaction silently rolled back because it has been
//        marked as rollback - only

//        /// Previously there was a error(transaction_type_check) in .save()  when there is that typeof exception the spring marks as rolled-back = true, but the commit only runs at the very end of the method so the java/spring confuses

    //       Here is the exact sequence of why the UnexpectedRollbackException happens:
    //
    //       depositTransaction starts Transaction A.
    //
    //       findByAccountNumber starts and finishes Transaction B completely independently. (All good here).
    //
    //       creditAmount joins Transaction A.
    //
    //       save joins Transaction A.
    //
    //       save hits the database constraint error (transaction_transaction_type_check) and throws a database exception.
    //
    //               Because save is sharing Transaction A with the parent, Spring intercepts that exception and permanently marks Transaction A as rollback-only. It is now poisoned.
    //
    //               Your Java code catches the exception with a try-catch block and swallows it.
    //
    //       Because the exception was swallowed, your depositTransaction method reaches the end successfully and tells Spring: "I'm done, please commit Transaction A!"
    //
    //       Spring tries to commit, sees the rollback-only flag left behind by the save method, and throws the UnexpectedRollbackException.

    @Transactional
    public ResponseEntity<ResponseDto<UUID>> depositTransaction(@Valid CreditRequestDto payload) {

        log.info("In Deposit payload: {}", payload);

        UUID userRequestKey = UUID.randomUUID();

        try {
            log.info("Verifying receiver account: [{}]", payload.receiverAccountNumber());
            accountRepository.findByAccountNumber(payload.receiverAccountNumber()).orElseThrow(() -> new EntityNotFoundException("Account not exist"));

            TransactionModel.TransactionType transactionType = (payload.transactionType() == TransactionModel.TransactionType.COMPENSATION)
                    ? TransactionModel.TransactionType.COMPENSATION
                    : TransactionModel.TransactionType.CREDIT;

            log.info("Checking for existing transaction with Correlation ID: [{}] and Type: [{}]", payload.correlationId(), transactionType);
            transactionRepository.findByCorrelationIdAndTransactionType(payload.correlationId(), transactionType)
                    .ifPresent(existing -> {
                        throw new EntityExistsException("Transaction already done");
                    });

            TransactionModel newTransaction = new TransactionModel();
            log.info("Transaction type from payload: {}", payload.transactionType());

            newTransaction.setTransactionType(transactionType);

            newTransaction.setCorrelationId(payload.correlationId());
            newTransaction.setAmount(payload.amount());
            newTransaction.setSender(payload.senderAccountNumber());
            newTransaction.setReceiver(payload.receiverAccountNumber());
            newTransaction.setSenderBank(payload.senderBank());

            log.info("Crediting amount to receiver and saving transaction record...");
            accountRepository.creditAmount(payload.receiverAccountNumber(), payload.amount());
            transactionRepository.save(newTransaction);

            log.info("Deposit Transaction done for Correlation ID: [{}]", payload.correlationId());

            return ResponseObj.success(200, "Deposit success", userRequestKey);
        } catch (EntityNotFoundException ee) {
            log.warn("Deposit rejected: Account not exist");
            return ResponseObj.error(400, "Account not exist");

        } catch (EntityExistsException e) {
            log.info("Deposit caught existing transaction. Returning success for Correlation ID: [{}]", payload.correlationId());
            return ResponseObj.success(200, "Credit transaction already succeed", null);

        } catch (Exception e) {
            log.error("Deposit transaction failed: {}", e.getMessage(), e);
            return ResponseObj.error(400, e.getMessage());
        }
    }


    @Transactional
    public ResponseEntity<ResponseDto<Void>> withdrawTransaction(@Valid DebitRequestDto payload) {

        log.info("In withdraw payload: {}", payload);

        try {
//            log.info("Locating original Transfer record for Correlation ID: [{}]", payload.correlationId());
//            TransferModel transferModel = transferRepository.findByCorrelationId(payload.correlationId()).orElseThrow(() -> new EntityNotFoundException("Transfer entity not fount"));

            log.info("Checking if DEBIT transaction already exists for Correlation ID: [{}]", payload.correlationId());
            boolean isExist = transactionRepository.existsByCorrelationIdAndTransactionStatusAndTransactionType(payload.correlationId(), TransactionModel.Status.SUCCESS, TransactionModel.TransactionType.DEBIT);

            if (isExist) {
                log.warn("DEBIT transaction already exists for Correlation ID: [{}]", payload.correlationId());
                throw new EntityExistsException("Transaction already exist");
            }

            TransactionModel newTransaction = new TransactionModel();

            newTransaction.setTransactionType(TransactionModel.TransactionType.DEBIT);
            newTransaction.setCorrelationId(payload.correlationId());
            newTransaction.setAmount(payload.amount());
            newTransaction.setSender(payload.accountNumber());
//            newTransaction.setReceiver(payload.);

//            System.out.println("\n\nThis is Destnimation : " + transferModel.getDestinationAccountNumber());
//            System.out.println("\n from payload : " + payload.accountNumber());

            newTransaction.setTransactionStatus(TransactionModel.Status.SUCCESS);

            log.info("Saving new DEBIT transaction and debiting balance for Account: [{}]", payload.accountNumber());
            transactionRepository.save(newTransaction);

            accountRepository.debitBalance(payload.accountNumber(), payload.amount());

            log.info("Withdraw success for Correlation ID: [{}]", payload.correlationId());
            return ResponseObj.success(200, "Withdraw success", null);
        } catch (EntityExistsException ee) {
            log.info("Withdraw caught existing transaction. Returning success for Correlation ID: [{}]", payload.correlationId());
            return ResponseObj.success(200, "Withdraw already processed", null);
        } catch (RuntimeException e) {
            log.error("Withdraw transaction failed: {}", e.getMessage(), e);
            return ResponseObj.error(400, e.getMessage());
        }

    }

    public ResponseEntity<?> transferTransaction(@Valid TransferRequestDto transferRequestDto) {

        UUID userRequestKey = UUID.randomUUID();

        log.info("Initiating new transfer request. Tracking Key: [{}]", userRequestKey);

        try {
            log.info("Verifying balance eligibility for Source Account: {}", transferRequestDto.senderAccountNumber());
            boolean isEligible = accountRepository.existsByAccountNumberAndBalanceGreaterThanEqual(
                    transferRequestDto.senderAccountNumber(), transferRequestDto.amount());

            if (!isEligible) {
                log.warn("Transfer rejected: Insufficient funds in Source Account: {}", transferRequestDto.senderAccountNumber());
                throw new ArithmeticException("Balance is too low for this transaction");
            }

            UserModel user = accountRepository.findByAccountNumber(transferRequestDto.senderAccountNumber()).get().getUser();
            log.info("Account verified. Authorized by User ID: {}", user.getId());

            log.info("Generating RS256 signed JWT token for secure Central Hub authentication...");
            String token = Jwts.builder()
                    .subject("NEXT_GEN")
                    .claim("senderAccountNumber", transferRequestDto.senderAccountNumber())
                    .claim("amount", transferRequestDto.amount())
                    .claim("receiverAccountNumber", transferRequestDto.receiverAccountNumber())
                    .claim("receiverBank", transferRequestDto.receiverBank())
                    .claim("receiverBank", transferRequestDto.receiverBank())
                    .signWith(privateKey, Jwts.SIG.RS256)
                    .compact();

            log.info("Token generated successfully. Preparing local PENDING transaction record.");

            TransferModel transactionModel = new TransferModel();
            transactionModel.setUser(user);
            transactionModel.setTransferStatus(TransferModel.status.PENDING);
            transactionModel.setAmount(transferRequestDto.amount());
            transactionModel.setDestinationAccountNumber(transferRequestDto.receiverAccountNumber());
            transactionModel.setDestinationBank(transferRequestDto.receiverBank());
            transactionModel.setSourceBank("NEXT_GEN");
            transactionModel.setSourceAccountNumber(transferRequestDto.senderAccountNumber());
            transactionModel.setUserRequestKey(userRequestKey);

            transferRepository.save(transactionModel);
            log.info("Local transaction record saved. Status: PENDING.");

            CentralHubTransferPayload payload = new CentralHubTransferPayload("NEXT_GEN", token, userRequestKey);

            System.out.println("This is payload grab it mate " + payload);

            log.info("Dispatching payload to Central Hub [POST /api/v1/transaction/testkafka]...");
            ResponseEntity<IncomingRequestDto<JsonNode>> response = restClient.post()
                    .uri("/api/v1/transaction/testkafka")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<IncomingRequestDto<JsonNode>>() {
                    });

            log.info("Successfully received response from Central Hub. HTTP Status: {}", response.getStatusCode());

            UUID correlationId = response.getBody().correlationId();
            log.info("Hub assigned Correlation ID: [{}]. Updating local database record...", correlationId);

            transactionModel.setCorrelationId(correlationId);
            transferRepository.save(transactionModel);

            log.info("Transaction sequence completed successfully for Tracking Key: [{}]", userRequestKey);

            return ResponseObj.success(200, "Success", null);

        } catch (RestClientResponseException | EntityExistsException | EntityNotFoundException e) {
            log.error("Transfer failed due to external system or data validation error: {}", e.getMessage());
            transferRepository.updateTransactionStatus(TransferModel.status.REJECTED, e.getMessage(), userRequestKey);
            return ResponseObj.error(401, e.getMessage());

        } catch (ArithmeticException e) {
            log.warn("Transfer aborted: {}", e.getMessage());
            return ResponseObj.error(402, e.getMessage());

        } catch (Exception e) {
            log.error("CRITICAL: Unexpected system failure during transfer for Tracking Key [{}]. Error: {}", userRequestKey, e.getMessage(), e);
            transferRepository.updateTransactionStatus(TransferModel.status.REJECTED, e.getMessage(), userRequestKey);
            return ResponseObj.error(500, e.getMessage());
        }
    }


    public ResponseEntity<?> transactionWebhook(WebhookTransferRequest payload) {
        UUID cid = payload.correlationId(); // Short variable for logging
        log.info("Processing status update for ID: {}", cid);

        try {
            TransferModel transferModel = transferRepository.findByCorrelationId(cid)
                    .orElseThrow(() -> new EntityNotFoundException("Record not found for ID: " + cid));

            transferModel.setTransferStatus(TransferModel.status.valueOf(payload.transferStatus()));
            transferModel.setErrorMsg(payload.errorMsg());
            transferRepository.save(transferModel);

            log.info("Transfer updated successfully to {} [ID: {}]", payload.transferStatus(), cid);
            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            log.error("Failed to process webhook for ID: {} | Reason: {}", cid, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}

