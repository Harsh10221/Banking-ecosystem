package com.centeral_hub.centeral_hub.controller;

import com.centeral_hub.centeral_hub.service.KafkaService;
import com.centeral_hub.centeral_hub.dtos.KafkaConsumerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    KafkaService kafkaService;

    public record ResponseDto(
            UUID correlationId
    ){}

    @PostMapping("/testkafka")
    public ResponseEntity<ResponseDto> test(@RequestBody @Valid KafkaConsumerDto payload) throws InterruptedException, JsonProcessingException {
//    public ResponseEntity<Map<String, UUID>> test(@RequestBody @Valid KafkaConsumerDto payload) throws InterruptedException, JsonProcessingException {

        UUID correlationId = UUID.randomUUID();
        System.out.println("\n Request received in transaction controller \n " + payload);
        payload.setCorrelationId(correlationId);

        kafkaService.sendTransactionToExecuteWithdraw(payload);

//        Map<String,UUID> map = Map.of("CorrelationId",correlationId);

        ResponseDto obj = new ResponseDto(correlationId);

        return ResponseEntity.status(202).body(obj);
    }


}
