package com.centeral_hub.centeral_hub.controller;

import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.service.KafkaConsumer;
import com.centeral_hub.centeral_hub.service.KafkaService;
import com.centeral_hub.centeral_hub.service.TransactionService;
import com.centeral_hub.centeral_hub.utils.JwtAuthentication;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

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
    @Autowired
    KafkaService kafkaService;

    @Autowired
    KafkaConsumer kafkaConsumer;

//    @Autowired
//    KafkaProducer kafkaProducer;

    @Autowired
    JwtAuthentication jwtAuthentication;

    @PostMapping("/transfer")
    public ResponseEntity<?> transferMethod(@RequestBody JsonNode payload) {

        System.out.println("This is payload" + payload);

        String senderAccountNo = payload.path("senderAccountNumber").asText(null);
        String senderBank = payload.path("senderBank").asText(null);
        BigDecimal amount = BigDecimal.valueOf(payload.path("amount").asLong(0));
        String type = payload.path("type").asText(null);
        String receiverAccountNumber = payload.path("receiverAccountNumber").asText(null);
        String receiverBank= payload.path("receiverBank").asText(null) ;
      String userRequestKey = payload.path("userRequestKey").asText(null);

        Map<String,Object> tokenBody = jwtAuthentication.jwtVerification(payload.get("token"));


        if(!((boolean) tokenBody.get("isVerified"))){
            return ResponseEntity.badRequest().body(tokenBody.get("Error"));
        }

        String bankToken = tokenBody.get("bankToken").toString();

        System.out.println("This is token body " + tokenBody);

        return transactionService.processInboundTransfer(senderAccountNo,senderBank,amount,type,receiverAccountNumber,receiverBank,bankToken,userRequestKey);

    }

    @PostMapping("/testkafka")
    public void test(@RequestBody JsonNode payload) throws InterruptedException {
        System.out.println("payload"+payload);
        String data = String.valueOf(payload);
        kafkaService.sendTransaction(data);
    }

    @PostMapping("/commit")
    public void commit(){
        kafkaConsumer.commitNow();
    }

//    @PostMapping("/send")
//    public String postTransaction(@RequestBody String payload){
//        kafkaProducer.sendTransaction(payload);
//        return "Transaction sent to Kafka!";
//    }


}
