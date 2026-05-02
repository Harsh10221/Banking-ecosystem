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

    /// There are two threads working together, a tomcat thread and kafka IO network bound thread which only work with newtowrkcard
    /// The tomcat thread(Main thread), reaches the kafka.send and call the kafkaIo thread and just go ahead for executing
    /// while in bg the kafka thread waiting and collection if there are more msg coming (Micro batching) if there were 1k request
    /// normally 1k tcp network connection but kafka thread sum all 1k into 1 single data.

//    private final AdminClient adminClient;
//
//    public KafkaService(AdminClient adminClient) {
//        this.adminClient = adminClient;
//    }

//    AtomicInteger successCount = new AtomicInteger(0);
//    AtomicInteger errorCount = new AtomicInteger(0);

    public void sendTransaction(String data) throws InterruptedException {
        System.out.println("I am from send transaction" + data);


        kafkaTemplate.send("transfer-transactions", "10",data)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Sent message offset: " + result.getRecordMetadata().offset());
//                        successCount.incrementAndGet();
                    } else {
//                        errorCount.incrementAndGet();
                        System.err.println("Unable to send message due to: " + ex.getMessage());
                    }
                });
    ///  Sleeping the main thread till then the kafka IO thread gets the resposne from the server
//            Thread.sleep(5000);
//        System.out.println("Successes: " + successCount.get());
//        System.out.println("Errors: " + errorCount.get());

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
