package com.centeral_hub.centeral_hub.utils;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class KafkaMonitorService {

    @Autowired
    private MeterRegistry meterRegistry;

    private final AdminClient adminClient;

    public KafkaMonitorService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    // Add this to your KafkaMonitorService
    public long getTrueRemaining(String groupId, String topic) throws Exception {
        // 1. Get the current position of the group (What is FINISHED)
        var offsets = adminClient.listConsumerGroupOffsets(groupId)
                .partitionsToOffsetAndMetadata().get();

        // 2. Get the end of the topic (What is PRODUCED)
        var topicPartitions = offsets.keySet();
        var endOffsets = adminClient.listOffsets(topicPartitions.stream()
                        .collect(Collectors.toMap(tp -> tp, tp -> OffsetSpec.latest())))
                .all().get();

        long totalLag = 0;
        for (var tp : topicPartitions) {
            long end = endOffsets.get(tp).offset();
            long current = offsets.get(tp).offset();
            totalLag += (end - current);
        }
        return totalLag;
    }
}