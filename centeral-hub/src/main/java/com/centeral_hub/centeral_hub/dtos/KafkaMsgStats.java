package com.centeral_hub.centeral_hub.dtos;

public record KafkaMsgStats(
        String type,
        data payload
) {
    public record data(
            long totalReq,
            long processed,
            long failed
    ){

    }
}
