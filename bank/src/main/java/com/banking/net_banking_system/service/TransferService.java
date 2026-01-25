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

    @Autowired
    private AccountService accountService;

    @Value("${next_gen.jwt.secret}")
    private String secretKey;

    public FormatDataToTransferCentralHub.DataObject initiateWithdrawTransfer(String senderAccountNo, BigDecimal amount, String type, String receiverAccountNumber, String receiverBank) {

        if (senderAccountNo == null || amount.compareTo(BigDecimal.ZERO) < 0 || !type.equals("Debit") ) {
            return null;
        }

//        String validationUser = accountService.validateRecipientAccount(senderAccountNo);
        ///This should be change because the validateREciept is returning an response of restclient.

        String  validationUser = "Success";
        if (!validationUser.equals("Success")) {
            throw new RuntimeException("User not ready for transfer");
        }



        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String verificationToken = Jwts.builder()
                .subject("NXT_GEN")
                .signWith(key)
                .compact();

        String senderBank = "Next_Gen";

        com.banking.net_banking_system.utils.FormatDataToTransferCentralHub.DataObject dataObject = FormatDataToTransferCentralHub.formatData(senderAccountNo,senderBank,amount,type,receiverAccountNumber,receiverBank,verificationToken);

//        String response = restClient.post()
//                .uri("http://localhost:8081/api/v1/ledger/transactions")
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(dataObject)
//                .retrieve()
//                .body(String.class);
//        in a banking system, you don't just want a String back; you want to know if the Central Hub successfully recorded the transaction. You can map the response directly back into a class.
//        System.out.println("Resoonse from centeral hub"+response);

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
