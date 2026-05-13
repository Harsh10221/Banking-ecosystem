package com.centeral_hub.centeral_hub.controller;

import com.centeral_hub.centeral_hub.dtos.KafkaMsgStats;
import com.centeral_hub.centeral_hub.dtos.TopicMetrics;
import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.service.BankService;
import com.centeral_hub.centeral_hub.service.KafkaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    KafkaService kafkaService;

    @Autowired
    BankService bankService;

    @Autowired
    ObjectMapper objectMapper;

    public record ResponseDto(
            UUID correlationId
    ) {
    }

    @Data
    public static class RequestDto { // This is a static nested class
        private String bankName;
        private String token;
        private UUID userRequestKey;
        private UUID correlationId;
    }

    public record TransactionStatusRequestDto(
            UUID correlationId
    ) {
    }


    @PostMapping("/transaction/info")
    public ResponseEntity<?> getTransactionUpdate(@RequestBody TransactionStatusRequestDto payload) {
        try {

            TransactionModel transactionModel = bankService.getTransactionUpdate(payload.correlationId);
            String data = objectMapper.writeValueAsString(transactionModel);
            return ResponseEntity.status(200).body(data);
        } catch (RuntimeException e) {

            log.error(e.getMessage());
            ResponseEntity.status(500).body(e.getMessage());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


        return null;
    }

    @PostMapping("/testkafka")
    public ResponseEntity<?> test(@RequestBody @Valid RequestDto payload) throws InterruptedException, JsonProcessingException {

        log.info("Received Central Hub dispatch request. Tracking Key: [{}], Source Bank: [{}]", payload.userRequestKey, payload.bankName);

        try {
            UUID correlationId = UUID.randomUUID();
            log.info("Hub assigned Correlation ID: [{}]. Linking to Tracking Key: [{}]", correlationId, payload.userRequestKey);

            payload.setCorrelationId(correlationId);

            log.info("Dispatching transaction to Kafka [Execute Withdraw Topic] for Correlation ID: [{}]...", correlationId);
            kafkaService.sendTransactionToExecuteWithdraw(payload);

            ResponseDto obj = new ResponseDto(correlationId);

            log.info("Successfully dispatched to Kafka. Returning HTTP 202 (Accepted) for Correlation ID: [{}]", correlationId);
            return ResponseEntity.status(202).body(obj);

        } catch (RuntimeException e) {
            log.error("CRITICAL: Runtime failure during Hub processing for Tracking Key: [{}]. Error: {}", payload.userRequestKey, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));

        }
    }


    @GetMapping("/kafka/metric/stats")
    public ResponseEntity<KafkaMsgStats> getKafkaStats() {
        TopicMetrics withdrawTopic = kafkaService.getCompleteTopicMetrics("execute-withdraw", "banking-group");
        TopicMetrics depositTopic = kafkaService.getCompleteTopicMetrics("execute-deposit", "banking-group");
        TopicMetrics compensationTopic = kafkaService.getCompleteTopicMetrics("execute-compensation", "banking-group");

        KafkaMsgStats responsePayload = new KafkaMsgStats("METRICS_RESULT", new KafkaMsgStats.data(withdrawTopic.totalOnDisk, depositTopic.successfullyProcessed, compensationTopic.successfullyProcessed));

        return ResponseEntity.ok().body(responsePayload);
    }

    ;
}
