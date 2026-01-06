package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.catalina.webresources.StandardRoot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import com.banking.net_banking_system.utils.FormatDataToTransferCentralHub;

@Controller
//@RestController
public class TransferService {

    RestClient restClient = RestClient.create();

    @Value("${next_gen.jwt.secret}")
    private String secretKey;

//    @Autowired
//    private FormatDataToTransferCentralHub.DataObject dataObject;

    public FormatDataToTransferCentralHub.DataObject initiateWithdrawTransfer(String senderAccountNo, BigDecimal amount, String type, String receiverAccountNumber, String receiverBank) {

//        System.out.println("senderAccount"+senderAccountNo);
//        System.out.println("amount "+amount.compareTo(BigDecimal.ZERO));
//        System.out.println("type"+!type.equals("Debit"));

        if (senderAccountNo == null || amount.compareTo(BigDecimal.ZERO) < 0 || !type.equals("Debit") ) {
//            return "Some required fields are null";
            return null;
        }

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String verificationToken = Jwts.builder()
                .subject("NXT_GEN")
                .signWith(key)
                .compact();

        String senderBank = "Next_Gen";
//      String verificationToken = "test";

        com.banking.net_banking_system.utils.FormatDataToTransferCentralHub.DataObject dataObject = FormatDataToTransferCentralHub.formatData(senderAccountNo,senderBank,amount,type,receiverAccountNumber,receiverBank,verificationToken);

        String response = restClient.post()
                .uri("http://localhost:8081/api/v1/ledger/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dataObject)
                .retrieve()
                .body(String.class);
//        in a banking system, you don't just want a String back; you want to know if the Central Hub successfully recorded the transaction. You can map the response directly back into a class.
        System.out.println("Resoonse from centeral hub"+response);

        //api for centeral hub
        // also need to authorize to check if it's bank only
        // Data = user,amount,type,from which bank ,
        // to which user/account no, to which bank
        // is the daily limit is reached



        // froze the money as soon as the initiatewithdrawtransfer is invoke

        //first withdraw if success then deposit in dest bank

        //response from centeral-hub then call the withdraw method

        //with the required data for it .








        return dataObject;
    }



}

//Request to centeral hub for transfer ->
//centeral hub / request is from valid source / can user have that much money ->
//centeral hub sends response to desti bank -> check if the user can take the money ->
//dest bank to centeral hub ready to take money ->
//sourse bank money debdit / withdraw method call ->
//dest bank ka deposit method call hogayega

//In real-world banking system the first step is manual

//Manual Registration: The Bank admin registers with the Central Hub (using a physical contract or a secure admin portal).
//
//The Secret Delivery: The Central Hub generates a Client ID and a Client Secret. These are like a "Username and Password" but for servers.
//
//Storage: The Bank stores these secrets in their server's environment variables (.env or application.properties). They never send these in a normal URL or share them.