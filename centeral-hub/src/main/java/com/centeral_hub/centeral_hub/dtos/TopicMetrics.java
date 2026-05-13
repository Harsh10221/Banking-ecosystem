package com.centeral_hub.centeral_hub.dtos;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class TopicMetrics {
    public long totalOnDisk;
    public long successfullyProcessed;
    public long consumerLag;

    public TopicMetrics(long totalOnDisk, long successfullyProcessed, long consumerLag) {
        this.totalOnDisk = totalOnDisk;
        this.successfullyProcessed = successfullyProcessed;
        this.consumerLag = consumerLag;
    }
}