package com.centeral_hub.centeral_hub.controller;

import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.service.TransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/ledger")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions")
    public String transferMethod(@RequestBody JsonNode payload) {
        // 1. Extract Data Correctly using the keys sent by the Bank
        String senderAccountNo = payload.get("senderAccountNo").asText();
        String senderBank = payload.get("senderBank").asText();
        BigDecimal amount = BigDecimal.valueOf(payload.get("amount").asLong());
        String type = payload.get("type").asText();
        
        String receiverAccountNo = payload.get("receiverAccountNo").asText(); 
        String receiverBank = payload.get("receiverBank").asText();
        String token = payload.get("token").asText();

        // 2. Build Model
        TransactionModel transactionModel = new TransactionModel();
        transactionModel.setSenderAccountNumber(senderAccountNo);
        transactionModel.setSenderBank(senderBank);
        transactionModel.setAmount(amount);
        transactionModel.setReceiverAccountNumber(receiverAccountNo);
        transactionModel.setReceiverBank(receiverBank);

        // 3. Process
        return transactionService.processInboundTransfer(transactionModel, token);
    }
}