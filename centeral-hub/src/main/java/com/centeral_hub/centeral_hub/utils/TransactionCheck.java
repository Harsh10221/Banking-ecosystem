package com.centeral_hub.centeral_hub.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Service
public class TransactionCheck {

//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;

    public enum TransactionStatus {
        NEW_REQUEST,
        ALREADY_PROCESSING
    }

    public TransactionStatus checkAndProcess(String key) {
//        Boolean isFirstRequest = redisTemplate.opsForValue()
//                .setIfAbsent(key, "PROCESSING", Duration.ofMinutes(5));
//
//        if (Boolean.TRUE.equals(isFirstRequest)) {
//            return TransactionStatus.NEW_REQUEST;
//        } else {
//            return TransactionStatus.ALREADY_PROCESSING;
//        }
        return TransactionStatus.NEW_REQUEST;


    }

}
