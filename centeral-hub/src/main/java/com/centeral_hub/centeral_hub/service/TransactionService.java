package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.model.MasterBalance;
import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.repository.MasterBalanceRepository;
import com.centeral_hub.centeral_hub.repository.TransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionService {


    //    for every bank there will be diffrent secret, the bank will send there token and there bank code "Bob"
//    then we will first try to decode the token if it works then we will use that as the senderbank name.
    @Value("${next_gen_bank_secret}")
    private String secretKeyOfNextBank;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RestClient restClient;

    @Autowired
    private MasterBalance masterBalance;

    @Autowired
    private MasterBalanceRepository masterBalanceRepository;

    UUID uuid = UUID.randomUUID();


    public String processInboundTransfer(String senderAccountNo, String senderBank, BigDecimal amount, String type, String receiverAccountNo, String receiverBank, String token) {

        if (senderAccountNo == null || senderBank == null || amount.compareTo(BigDecimal.ZERO) <= 0 || !type.equals("Debit") || receiverAccountNo == null || receiverBank == null || token == null) {
            return "feilds are required";
        }

        TransactionModel transactionModel = new TransactionModel();

        transactionModel.setSenderAccountNumber(senderAccountNo);
        transactionModel.setSenderBank(senderBank);
        transactionModel.setReceiverBank(receiverBank);
        transactionModel.setReceiverAccountNumber(receiverAccountNo);
        transactionModel.setAmount(amount);
        transactionModel.setSenderAccountNumber(senderAccountNo);
        transactionModel.setCorrelationId(uuid);

        try {
            TransactionModel savedTransaction = transactionRepository.save(transactionModel);

            System.out.println("This is obj" + savedTransaction);

            UUID correlationId = savedTransaction.getCorrelationId();

            if (correlationId == null) {
                return "Transaction saved, but Correlation ID is missing.";
            }

            SecretKey key = Keys.hmacShaKeyFor(secretKeyOfNextBank.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parser()
                    .verifyWith(key) // Use the same key used for signing
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            System.out.println("claims" + claims.getSubject());

//            Both side same NXT_GEN  ::
            claims.getSubject();

            //Start the transction from here only

            JsonNode accountValidateResponse = restClient.post()
                    .uri("/account/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("accountNo", receiverAccountNo))
                    .retrieve()
//                    .body(receiverAccountNo)

//                    .body(String.class); previsouly this was used why and what it did ?
                    .body(JsonNode.class);

            System.out.println("Api response" + accountValidateResponse);
            String status = accountValidateResponse.get("statusCode").asText();
            System.out.println("account validate status" + status);

            //withdraw
            var withdrawDataBody = Map.of(
                    "accountNumber", senderAccountNo,
                    "type", "Withdraw",
                    "amount", amount
//                    "userId", "2"
            );

            /// check if there any better way to do this object things insted
            ////     insted of doing this what is the better and best approach also

            /// Log this in the db as withdraw req initiated , when done update as done
            JsonNode withdrawInitiateRequest = restClient.post()
                    .uri("/api/transaction/withdraw")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(withdrawDataBody)
                    .retrieve()
                    .body(JsonNode.class);

            System.out.println("Withdraw request" + withdrawInitiateRequest);
            String withdrawStatus = withdrawInitiateRequest.get("statusCode").asText();
            System.out.println("Withdraw status" + withdrawStatus);

            //// if the response of the bank is success then log that and send request for deposit in the bank b

            if (!withdrawInitiateRequest.get("statusCode").asText().equals("200")){
                /// Update the transaction as rejected with the msg ,
                return "Error in the withdraw";
            }

            ///update the transaction as success and insert a transaction as deposit initiated

            var depositDataBody = Map.of(
                    "accountNumber", receiverAccountNo,
                    "type", "Deposit",
                    "amount", amount
//                    "userId", "2"
            );

//            String depositUri = UriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/transaction/deposit")
//                    .build()
//                    .toUriString();

            JsonNode depositInitiateRequest = restClient.post()
                    .uri("/api/transaction/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(depositDataBody)
                    .retrieve()
                    .body(JsonNode.class);

            System.out.println("Deposit request" + depositInitiateRequest);
            String depositStatus = depositInitiateRequest.get("statusCode").asText();
            System.out.println("deposit statusCode" + depositStatus);

            if (!depositStatus.equals("200")){
                ///update the transaction as rejected and add message;
                return "error in deposit";

            }





            ///update the transaction as success;



        } catch (Exception e) {
            throw new RuntimeException(e);
//       return "Error: failure - " + e.getMessage();
        }


        return "success";
    }

    public String dispatchOutboundTransfer() {

        return "success";
    }

}

