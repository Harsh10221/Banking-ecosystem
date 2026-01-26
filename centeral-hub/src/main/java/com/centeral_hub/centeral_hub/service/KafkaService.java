package com.centeral_hub.centeral_hub.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final AdminClient adminClient;

    public KafkaService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger errorCount = new AtomicInteger(0);

    public void sendTransaction(String data) throws InterruptedException {
        System.out.println("I am from sendtransaction" + data);


        kafkaTemplate.send("transfer-transactions", "10", data)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message offset: " + result.getRecordMetadata().offset());
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                        System.err.println("Unable to send message due to: " + ex.getMessage());
                    }
                });

        Thread.sleep(5000);
        System.out.println("Successes: " + successCount.get());
        System.out.println("Errors: " + errorCount.get());

    }


//    @Scheduled(fixedRate = 5000)
//    public void monitorQueue() {
//        try {
//            var offsets = adminClient.listConsumerGroupOffsets("banking-group").partitionsToOffsetAndMetadata().get();
//            var topicPartitions = offsets.keySet();
//            var endOffsets = adminClient.listOffsets(topicPartitions.stream().collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.latest()))).all().get();
//
//            long totalLag = 0;
//            for (var tp : topicPartitions) {
//                long end = endOffsets.get(tp).offset();
//                long current = offsets.get(tp).offset();
//                totalLag += (end - current);
//                // ADD THIS PRINT LINE
//                System.out.println("Partition " + tp.partition() + " | End: " + end + " | Current: " + current);
//            }
//            System.out.println("--- TOTAL LAG: " + totalLag);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
