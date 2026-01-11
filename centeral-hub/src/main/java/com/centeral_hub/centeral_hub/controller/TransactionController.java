package com.centeral_hub.centeral_hub.controller;

import com.centeral_hub.centeral_hub.service.TransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transfer")
    public String transferMethod(@RequestBody JsonNode payload) {

        System.out.println("This is payload" + payload);

        String senderAccountNo = payload.get("senderAccountNo").asText();
        String senderBank = payload.get("senderBank").asText();
        BigDecimal amount = BigDecimal.valueOf(payload.get("amount").asLong());
        String type = payload.get("type").asText();
        String receiverAccountNumber = payload.get("receiverAccountNumber").asText();
        String receiverBank= payload.get("receiverBank").asText();
        String token = payload.get("token").asText();

//Change the datatype of the correlation id to uuid from string
//        TransactionService transactionService = new TransactionService();

        return transactionService.processInboundTransfer(senderAccountNo,senderBank,amount,type,receiverAccountNumber,receiverBank,token);
//        return "success";

    }

}
