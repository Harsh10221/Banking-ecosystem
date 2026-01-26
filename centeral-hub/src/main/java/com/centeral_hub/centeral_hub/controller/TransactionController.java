package com.centeral_hub.centeral_hub.controller;

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
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

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
