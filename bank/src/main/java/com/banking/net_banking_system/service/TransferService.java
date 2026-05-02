//package com.banking.net_banking_system.service;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.client.RestClient;
//
//import javax.crypto.SecretKey;
//import java.math.BigDecimal;
//import java.nio.charset.StandardCharsets;
//
//import com.banking.net_banking_system.utils.FormatDataToTransferCentralHub;
//
//@Controller
////@RestController
//public class TransferService {
//
//    RestClient restClient = RestClient.create();
//
//    @Autowired
//    private AccountService accountService;
//
//    @Value("${next_gen.jwt.secret}")
//    private String secretKey;
//
//    public FormatDataToTransferCentralHub.DataObject initiateWithdrawTransfer(String senderAccountNo, BigDecimal amount, String type, String receiverAccountNumber, String receiverBank) {
//
//        if (senderAccountNo == null || amount.compareTo(BigDecimal.ZERO) < 0 || !type.equals("Debit") ) {
//            return null;
//        }
//
//        String  validationUser = "Success";
//        if (!validationUser.equals("Success")) {
//            throw new RuntimeException("User not ready for transfer");
//        }
//
//        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
//
//        String verificationToken = Jwts.builder()
//                .subject("NXT_GEN")
//                .signWith(key)
//                .compact();
//
//        String senderBank = "Next_Gen";
//
//        com.banking.net_banking_system.utils.FormatDataToTransferCentralHub.DataObject dataObject = FormatDataToTransferCentralHub.formatData(senderAccountNo,senderBank,amount,type,receiverAccountNumber,receiverBank,verificationToken);
//
//
//        return dataObject;
//    }
//
//
//
//}
