package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.controller.TransactionController;
import com.centeral_hub.centeral_hub.dtos.KafkaConsumerDto;
import com.centeral_hub.centeral_hub.dtos.KafkaMsgStats;
import com.centeral_hub.centeral_hub.dtos.TopicMetrics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;


    /// There are two threads working together, a tomcat thread and kafka IO network bound thread which only work with newtowrkcard
    /// The tomcat thread(Main thread), reaches the kafka.send and call the kafkaIo thread and just go ahead for executing
    /// while in bg the kafka thread waiting and collection if there are more msg coming (Micro batching) if there were 1k request
    /// normally 1k tcp network connection but kafka thread sum all 1k into 1 single data.


    public void sendTransactionToExecuteWithdraw(TransactionController.RequestDto payload) throws InterruptedException, JsonProcessingException {
        System.out.println("\n Send Transaction to Withdraw \n " + payload);

        String msg = objectMapper.writeValueAsString(payload);

        kafkaTemplate.send("execute-withdraw", msg)
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


//    public KafkaMsgStats getAllTopicMessageCountAndFormat() {
//        long withdrawMsgCount = getTopicMessageCount("execute-withdraw");
//        long depositMsgCount = getCommittedOffsetCount("execute-deposit", "banking-group");
//        long compensationMsgCount = getCommittedOffsetCount("execute-compensation", "banking-group");
//
//        System.out.println("\nwithdraw " + withdrawMsgCount);
//        System.out.println("\ndeposit " + depositMsgCount);
//        System.out.println("\ncompensation " + compensationMsgCount);
//
//
//        return new KafkaMsgStats(withdrawMsgCount,depositMsgCount,compensationMsgCount);
//
//    }


    public TopicMetrics getCompleteTopicMetrics(String topicName, String groupId) {
        try (Consumer<String, String> consumer = consumerFactory.createConsumer(groupId, null)) {

            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);

            // 🚨 DIAGNOSTIC 1: Check for Silent Metadata Failure
            if (partitionInfos == null || partitionInfos.isEmpty()) {
                System.out.println("❌ METADATA FAILURE: Broker returned 0 partitions for topic '" + topicName + "'");
                return new TopicMetrics(0, 0, 0);
            }

            Set<TopicPartition> partitions = partitionInfos.stream()
                    .map(info -> new TopicPartition(info.topic(), info.partition()))
                    .collect(Collectors.toSet());

            Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(partitions);
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
            Map<TopicPartition, OffsetAndMetadata> committedOffsets = consumer.committed(partitions);

            long totalOnDisk = 0;
            long totalProcessed = 0;
            long totalLag = 0;

            System.out.println("\n📊 --- DIAGNOSTICS FOR: " + topicName + " ---");

            for (TopicPartition partition : partitions) {
                long begin = beginningOffsets.getOrDefault(partition, 0L);
                long end = endOffsets.getOrDefault(partition, 0L);

                OffsetAndMetadata metadata = committedOffsets.get(partition);
                long committedRaw = (metadata != null) ? metadata.offset() : -1; // -1 means no commit yet
                long committed = (metadata != null) ? metadata.offset() : begin;
                committed = Math.max(committed, begin);

                // 🚨 DIAGNOSTIC 2: Print the raw pointers per partition
                System.out.printf("Partition %d | Begin: %d | End: %d | Committed: %d%n",
                        partition.partition(), begin, end, committedRaw);

                totalOnDisk += (end - begin);
                totalProcessed += (committed - begin);
                totalLag += (end - committed);
            }
            System.out.println("-----------------------------------------\n");

            return new TopicMetrics(totalOnDisk, totalProcessed, totalLag);
        }
    }


//    public long getTopicMessageCount(String topicName) {
//        try (Consumer<String, String> consumer = consumerFactory.createConsumer()) {
//
//            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
//
//            // 1. Check if the topic even exists or is visible
//            if (partitionInfos == null || partitionInfos.isEmpty()) {
//                return 0L;
//            }
//
//            List<TopicPartition> partitions = partitionInfos.stream()
//                    .map(info -> new TopicPartition(info.topic(), info.partition()))
//                    .collect(Collectors.toList());
//
//            Map<TopicPartition, Long> beginningOffsets = consumer.beginningOffsets(partitions);
//            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
//
//            long totalMessages = 0;
//
//            for (TopicPartition partition : partitions) {
//                long begin = beginningOffsets.getOrDefault(partition, 0L);
//                long end = endOffsets.getOrDefault(partition, 0L);
//                long activeMessages = end - begin;
//
//                // 2. Expose the raw offset math
//
//                totalMessages += activeMessages;
//            }
//
//            return totalMessages;
//        }
//    }

//    public long getCommittedOffsetCount(String topicName, String groupId) {
//        // We pass the groupId to the factory so Kafka knows whose pointer to look for.
//        // The "null" is for the client suffix, which we don't need for a quick lookup.
//        try (Consumer<String, String> consumer = consumerFactory.createConsumer(groupId, null)) {
//
//            List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
//
//            if (partitionInfos == null || partitionInfos.isEmpty()) {
//                return 0L;
//            }
//
//            // Convert to a Set of TopicPartitions (required by the committed() method)
//            Set<TopicPartition> partitions = partitionInfos.stream()
//                    .map(info -> new TopicPartition(info.topic(), info.partition()))
//                    .collect(Collectors.toSet());
//
//            // Fetch the exact pointers for this specific consumer group
//            Map<TopicPartition, OffsetAndMetadata> committedOffsets = consumer.committed(partitions);
//
//            long totalCommitted = 0;
//
//            for (TopicPartition partition : partitions) {
//                OffsetAndMetadata metadata = committedOffsets.get(partition);
//
//                // If metadata is not null, the consumer has processed and acked messages here
//                if (metadata != null) {
//                    totalCommitted += metadata.offset();
//                }
//            }
//
//            return totalCommitted;
//        }
//
//
//    }

}