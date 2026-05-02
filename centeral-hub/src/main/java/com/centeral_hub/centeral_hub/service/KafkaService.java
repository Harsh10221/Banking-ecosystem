package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.dtos.KafkaConsumerDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /// There are two threads working together, a tomcat thread and kafka IO network bound thread which only work with newtowrkcard
    /// The tomcat thread(Main thread), reaches the kafka.send and call the kafkaIo thread and just go ahead for executing
    /// while in bg the kafka thread waiting and collection if there are more msg coming (Micro batching) if there were 1k request
    /// normally 1k tcp network connection but kafka thread sum all 1k into 1 single data.


    public void sendTransactionToExecuteWithdraw(KafkaConsumerDto payload) throws InterruptedException, JsonProcessingException {
        System.out.println("\n Send Transaction to Withdraw \n " + payload);

        String key = payload.getSenderAccountNumber();
        String msg = objectMapper.writeValueAsString(payload);

        kafkaTemplate.send("execute-withdraw", key, msg)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message offset: " + result.getRecordMetadata().offset());
                    } else {
                        System.err.println("Unable to send message due to: " + ex.getMessage());
                    }
                });

    }

    public void sendTransactionToExecuteDeposit(KafkaConsumerDto payload) throws InterruptedException, JsonProcessingException {
        System.out.println("\n Send Transaction to Deposit \n " + payload);

        String key = payload.getReceiverAccountNumber();
        String msg = objectMapper.writeValueAsString(payload);

        kafkaTemplate.send("execute-deposit", key, msg)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message offset: " + result.getRecordMetadata().offset());
                    } else {
                        System.err.println("Unable to send message due to: " + ex.getMessage());
                    }
                });


    }

    public void sendTransactionToExecuteCompensation(KafkaConsumerDto payload) throws InterruptedException, JsonProcessingException {
        System.out.println("\n Send Transaction to Compensation \n " + payload);

        String key = payload.getSenderAccountNumber();
        String msg = objectMapper.writeValueAsString(payload);

        kafkaTemplate.send("execute-compensation", key, msg)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message offset: " + result.getRecordMetadata().offset());
                    } else {
                        System.err.println("Unable to send message due to: " + ex.getMessage());
                    }
                });


    }

}