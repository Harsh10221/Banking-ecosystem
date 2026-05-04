package com.banking.net_banking_system.service;

import com.banking.net_banking_system.dtos.CentralHubTransferPayload;
import com.banking.net_banking_system.dtos.CreditRequestDto;
import com.banking.net_banking_system.dtos.DebitRequestDto;
import com.banking.net_banking_system.dtos.TransferRequestDto;
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
public class TransactionService {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestClient restClient;

    @Autowired
    private PrivateKey privateKey;


    @Transactional
    public ResponseEntity<ResponseDto<UUID>> depositTransaction(@Valid CreditRequestDto payload) {

        System.out.println("\n In Deposit payload \n" + payload);

        UUID userRequestKey = UUID.randomUUID();

//        2026 - 04 - 30 T00:
//        12:50.674 + 05:30 ERROR 10496-- - [nio - 8080 - exec - 4]o.a.c.c.C.[.[.[/].[dispatcherServlet]    :
//        Servlet.service() for servlet[dispatcherServlet] in context with path[] threw exception[ Request processing
//        failed:
//        org.springframework.transaction.UnexpectedRollbackException:Transaction silently rolled back because it has been
//        marked as rollback - only]with root cause
//
//        org.springframework.transaction.UnexpectedRollbackException:Transaction silently rolled back because it has been
//        marked as rollback - only

        try {
            accountRepository.findByAccountNumber(payload.receiverAccountNumber()).orElseThrow(() -> new EntityNotFoundException("Account not exist"));

            TransactionModel.TransactionType transactionType = (payload.transactionType() == TransactionModel.TransactionType.COMPENSATION)
                    ? TransactionModel.TransactionType.COMPENSATION
                    : TransactionModel.TransactionType.CREDIT;


            transactionRepository.findByCorrelationIdAndTransactionType(payload.correlationId(), transactionType)
                    .ifPresent(existing -> {
                        throw new EntityExistsException("Transaction already done");
                    });


            TransactionModel newTransaction = new TransactionModel();
            System.out.println("Transaction type from payload" + payload.transactionType());


            newTransaction.setTransactionType(transactionType);

            newTransaction.setCorrelationId(payload.correlationId());
            newTransaction.setAmount(payload.amount());
            newTransaction.setSender(payload.senderAccountNumber());
            newTransaction.setReceiver(payload.receiverAccountNumber());
            newTransaction.setSenderBank(payload.senderBank());

            accountRepository.creditAmount(payload.receiverAccountNumber(), payload.amount());
            transactionRepository.save(newTransaction);

            System.out.println("Deposit Transaction done");

            return ResponseObj.success(200, "Deposit success", userRequestKey);
        } catch (EntityNotFoundException ee) {
            return ResponseObj.error(400, "Account not exist");

        } catch (EntityExistsException e) {
            return ResponseObj.success(200, "Credit transaction already succeed", null);

        } catch (Exception e) {
            return ResponseObj.error(400, e.getMessage());
        }


        /// Previously there was a error(transaction_type_check) in .save() when there is that type of exception the spring marks as rolled-back = true, but the commit only runs at the very end of the method so the java/spring confuses
//        Here is the exact sequence of why the UnexpectedRollbackException happens:
//
//        depositTransaction starts Transaction A.
//
//        findByAccountNumber starts and finishes Transaction B completely independently. (All good here).
//
//        creditAmount joins Transaction A.
//
//        save joins Transaction A.
//
//        save hits the database constraint error (transaction_transaction_type_check) and throws a database exception.
//
//                Because save is sharing Transaction A with the parent, Spring intercepts that exception and permanently marks Transaction A as rollback-only. It is now poisoned.
//
//                Your Java code catches the exception with a try-catch block and swallows it.
//
//        Because the exception was swallowed, your depositTransaction method reaches the end successfully and tells Spring: "I'm done, please commit Transaction A!"
//
//        Spring tries to commit, sees the rollback-only flag left behind by the save method, and throws the UnexpectedRollbackException.


    }

    @Transactional
    public ResponseEntity<ResponseDto<Void>> withdrawTransaction(@Valid DebitRequestDto payload) {

        try {
            System.out.println("\n In withdraw payload \n" + payload);

            TransferModel transferModel = transferRepository.findByCorrelationId(payload.correlationId()).orElseThrow(() -> new EntityNotFoundException("Transfer entity not fount"));

            boolean isExist = transactionRepository.existsByCorrelationIdAndTransactionStatusAndTransactionType(payload.correlationId(), TransactionModel.Status.SUCCESS, TransactionModel.TransactionType.DEBIT);
            if (isExist) throw new EntityExistsException("Transaction already exist");

            TransactionModel newTransaction = new TransactionModel();

            newTransaction.setTransactionType(TransactionModel.TransactionType.DEBIT);
            newTransaction.setCorrelationId(payload.correlationId());
            newTransaction.setAmount(payload.amount());
            newTransaction.setSender(payload.accountNumber());
            newTransaction.setReceiver(transferModel.getDestinationAccountNumber());
            newTransaction.setTransactionStatus(TransactionModel.Status.SUCCESS);

            transactionRepository.save(newTransaction);

            accountRepository.debitBalance(payload.accountNumber(), payload.amount());

            return ResponseObj.success(200, "Withdraw success", null);
        } catch (EntityExistsException ee) {
            return ResponseObj.success(200, "Withdraw already processed", null);
        } catch (RuntimeException e) {
            return ResponseObj.error(400, e.getMessage());
        }

    }

    public ResponseEntity<?> transferTransaction(@Valid TransferRequestDto transferRequestDto) {

        UUID userRequestKey = UUID.randomUUID();
        try {

            boolean isEligible = accountRepository.existsByAccountNumberAndBalanceGreaterThanEqual(transferRequestDto.senderAccountNumber(), transferRequestDto.amount());

            UserModel user = accountRepository.findByAccountNumber(transferRequestDto.senderAccountNumber()).get().getUser();

            if (!isEligible) throw new ArithmeticException("Balance is too low for this transaction");

//            PrivateKey privateKey = getPrivateKey.getPrivateKeyFromString();

            String token = Jwts.builder()
                    .subject("NEXT_GEN")
                    .claim("senderAccountNumber", transferRequestDto.senderAccountNumber())
                    .claim("amount", transferRequestDto.amount())
                    .claim("receiverAccountNumber", transferRequestDto.receiverAccountNumber())
                    .claim("receiverBank", transferRequestDto.receiverBank())
                    .claim("receiverBank", transferRequestDto.receiverBank())
                    .signWith(privateKey, Jwts.SIG.RS256)
                    .compact();

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

            CentralHubTransferPayload payload = new CentralHubTransferPayload("NEXT_GEN", token, userRequestKey);

            ResponseEntity<IncomingRequestDto<JsonNode>> response = restClient.post()
                    .uri("/api/v1/transaction/testkafka")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<IncomingRequestDto<JsonNode>>() {
                    });

            //May not work as expected but should be check it could be work
            UUID correlationId = response.getBody().correlationId();
            transactionModel.setCorrelationId(correlationId);
            transferRepository.save(transactionModel);


            System.out.println("End response  " + response);

            return ResponseObj.success(200, "Success", null);

        } catch (RestClientResponseException | EntityExistsException | EntityNotFoundException e) {
            transferRepository.updateTransactionStatus(TransferModel.status.REJECTED, e.getMessage(), userRequestKey);
            return ResponseObj.error(401, e.getMessage());

        } catch (ArithmeticException e) {
            return ResponseObj.error(402, e.getMessage());
        } catch (Exception e) {
            transferRepository.updateTransactionStatus(TransferModel.status.REJECTED, e.getMessage(), userRequestKey);
            return ResponseObj.error(500, e.getMessage());

        }
    }


//    public ResponseEntity<?> transactionWebhook(Long senderAccountNumber, BigDecimal amount, Long destinationAccountNumber, String destinationBank, String transactionStatus, String errorMsg) {
//
//        Transaction.status transactionStatusChangeTo = Transaction.status.REJECTED;
//
//        if (transactionStatus.equalsIgnoreCase("True")) {
//            transactionStatusChangeTo = Transaction.status.APPROVED;
//        }
//
//        try {
//
//            User user = userRepository.findByAccountDetailsAccountNumber(String.valueOf(senderAccountNumber))
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            transactionRepository.updateTransactionStatus();
//
//
//            System.out.println("I am here mate in webhook try block ");
//            return ResponseEntity.ok().body(Map.of("suatus", "success"));
//        } catch (RuntimeException e) {
//            System.out.println("I am here mate in webhook Catch block ");
//            return ResponseEntity.badRequest().body(Map.of("Error", e.getMessage()));
//
//        }
//
//
//    }

}

