package com.banking.net_banking_system.service;

import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.model.Transaction;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.AccountRepository;
import com.banking.net_banking_system.repository.TransactionRepository;
import com.banking.net_banking_system.repository.UserRepository;
import com.banking.net_banking_system.utils.FormatDataToTransferCentralHub;
import com.banking.net_banking_system.utils.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import javax.crypto.SecretKey;
import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RestClient restClient;

    @Value("${NEXT_GEN_SECRET}")
    private String secretKey;


    @Transactional
    public ResponseEntity<?> depositTransaction(String accountNumber, String type, Long amount, Long userId) {

        if (accountNumber == null || !type.equals("Deposit") || amount == null) {
//            return ResponseObject.createResponse(400, "Account number and amount are required or type invalid.", null, HttpStatus.BAD_REQUEST);
             return ResponseEntity.badRequest().body("Account number or amount are required or type is invalid");
        }

        if (amount < 1) {
//            return ResponseObject.createResponse(400, "Deposit amount must be at least 1.", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body("Deposit amount must be at least 1");
        }

        Transaction newTransaction = new Transaction();

        //// Might not needed if not needed remove this
        User userObj = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id:" + userId));

        if (!userObj.getAccountDetails().getAccountNumber().equals(accountNumber)) {
//            return ResponseObject.createResponse(404, "Account and userId not match", null, HttpStatus.NOT_FOUND);
            return ResponseEntity.badRequest().body("Account and userId not matched");
        }

        newTransaction.setUser(userObj);
        newTransaction.setTransactionType(Transaction.Type.CREDIT);
        newTransaction.setTransactionStatus(Transaction.status.APPROVED);
        newTransaction.setAmount(BigDecimal.valueOf(amount));

        AccountDetails accountDetails = userObj.getAccountDetails();

        accountDetails.setBalance(accountDetails.getBalance().add(BigDecimal.valueOf(amount)));
        Transaction result = transactionRepository.save(newTransaction);


//        return ResponseObject.createResponse(400, "Deposit failed", null, HttpStatus.BAD_REQUEST);
//        return ResponseObject.createResponse(200, "Deposit success", null, HttpStatus.OK);
        return ResponseEntity.ok().body("Deposit success");
    }

    @Transactional
//    public ResponseEntity<ResponseObject<String>> withdrawTransaction(String accountNumber, String type, Long amount, Long userId) {
    public ResponseEntity<?> withdrawTransaction(String accountNumber, String type, Long amount, Long userId) {
//        System.out.println("I am from withdraw");
        Transaction newTransaction = new Transaction();

        if (accountNumber == null || !type.equals("Withdraw") || amount == null) {
//            return "Account No or amount are required";
//            return ResponseObject.createResponse(404, "Account No or Amount is required", null, HttpStatus.NOT_FOUND);
            return ResponseEntity.badRequest().body( "Account No or Amount is required");
//            .( "Account No or Amount is required");
        }

        if (amount < 1) {
//            return "Amount should be greater than 0";
//            ResponseObject.createResponse(400, "Minimum amount should be 1 ", null, HttpStatus.BAD_REQUEST);
           return ResponseEntity.badRequest().body("Minimum amount should be 1");

        }

        //// Might not need this remove this if not necessaray
        User userObj = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id:" + userId));


        if (!userObj.getAccountDetails().getAccountNumber().equals(accountNumber)) {
//            System.out.println("I am inside no match");
//            ResponseObject.createResponse(400, "Account Number and User Id are not matched", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body("Account number and User Id are not matched");

        }


//        When the balance is low from the required amount the money still depositing in the reciver
//        Issue is becasue of the exception handling the return as string response is considering true
        if (userObj.getAccountDetails().getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            System.out.println("I am inside low balance");
//            return ResponseObject.createResponse(400, "Balance is low for transaction ", null, HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().body("Balance is low for transaction ");
        }


        newTransaction.setUser(userObj);
        newTransaction.setAmount(BigDecimal.valueOf(amount));
        newTransaction.setType(Transaction.Type.DEBIT);
        newTransaction.setTransactionStatus(Transaction.status.APPROVED);


//        AccountDetails accountDetails = userObj.getAccountDetails();

        int resultDb = accountRepository.substractBalance(accountNumber, amount);
        System.out.println("This is result Db" + resultDb);
        Transaction result = transactionRepository.save(newTransaction);

//        return ResponseObject.createResponse(400, "Withdraw Failed", null, HttpStatus.BAD_REQUEST);
//        return ResponseObject.createResponse(200, "Withdraw success", null, HttpStatus.OK);
        return  ResponseEntity.ok().body("Success");
    }

    public ResponseEntity<?> transferTransaction(String senderAccountNumber, BigDecimal amount, String type, Long receiverAccountNumber, String receiverBank, Long userId) {

        if (senderAccountNumber == null || amount.compareTo(BigDecimal.ONE) <= 0 || receiverAccountNumber == null || !type.equals("TRANSFER") || receiverBank == null || userId == null) {
            var responseBody = Map.of(
                    "Error", "Fields are required"
            );
            return ResponseEntity.badRequest().body(responseBody);
        }
        /// Send a detailed msg body and then use hash map in the centeral hub and decode that and use.
        try {

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Error: amount should be greater than 0");
            }

            User user = userRepository.findById(userId).orElseThrow(
                    () -> new RuntimeException("User not found with id: " + userId)
            );

            BigDecimal balance = user.getAccountDetails().getBalance();

            if (balance.compareTo(amount) < 0) {
                return ResponseEntity.badRequest().body("Error: balance is too low for this transaction");
            }

            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            String bankToken = Jwts.builder()
                    .subject("NEXT_GEN")
                    .signWith(key)
                    .compact();

            var tokenBody = Map.of(
                    "Issuer", "NEXT_GEN",
                    "token", bankToken
            );

            var requestBody = Map.of(
                    "senderAccountNumber", senderAccountNumber,
                    "amount", amount,
                    "type", "Debit",
                    "receiverAccountNumber", receiverAccountNumber,
                    "receiverBank", receiverBank,
                    "token", tokenBody,
                    "userRequestKey", "123456"  //Remove this the token and userReqkey
            );

            ResponseEntity<JsonNode> response = restClient.post()
                    .uri("/api/v1/transaction/testkafka")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .toEntity(JsonNode.class);
            int statusCode = response.getStatusCode().value();

            JsonNode body = response.getBody();

            if (statusCode != 200) {
                return ResponseEntity.badRequest().body(Map.of("Msg", "Central hub api", "Error", body.get("message")));
            }

            Transaction transaction = new Transaction();

            transaction.setUser(user);
            transaction.setTransactionType(Transaction.Type.TRANSFER);
            transaction.setTransactionStatus(Transaction.status.PENDING);
            transaction.setAmount(amount);
            transaction.setDestinationAccountNumber(receiverAccountNumber);
            transaction.setDestinationBank(receiverBank);

            transaction.setSourceBank("NEXT_GEN");
            transaction.setSourceAccountNumber(Long.parseLong(senderAccountNumber));

            System.out.println("This is transaction Obj" + transaction);

            transactionRepository.save(transaction);

            System.out.println("Response " + response);

            return ResponseEntity.ok().body(Map.of("Msg", "Success"));
        } catch (RuntimeException e) {
            System.out.println("I am here mate" + e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    public ResponseEntity<?> transactionWebhook(Long senderAccountNumber, BigDecimal amount, Long destinationAccountNumber, String destinationBank, String transactionStatus, String errorMsg) {

        Transaction.status transactionStatusChangeTo = Transaction.status.REJECTED;

        if (transactionStatus.equalsIgnoreCase("True")) {
            transactionStatusChangeTo = Transaction.status.APPROVED;
        }

        try {

            User user = userRepository.findByAccountDetailsAccountNumber(String.valueOf(senderAccountNumber))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            transactionRepository.updateTransactionStatus(transactionStatusChangeTo, user, destinationAccountNumber, destinationBank, amount, errorMsg);


            System.out.println("I am here mate in webhook try block ");
            return ResponseEntity.ok().body(Map.of( "suatus","success"));
        } catch (RuntimeException e) {
            System.out.println("I am here mate in webhook Catch block ");
            return ResponseEntity.badRequest().body(Map.of("Error",e.getMessage()));

        }


    }

}


//public enum SettlementStatus {
//    SUCCESS,
//    REJECTED,
//    FAILED_TECHNICAL
//}
//
//@Column(nullable = false)
//@Enumerated(EnumType.STRING)
//private SettlementStatus settlementStatus;
