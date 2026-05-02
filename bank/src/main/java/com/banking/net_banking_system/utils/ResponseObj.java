package com.banking.net_banking_system.utils;

import org.springframework.http.ResponseEntity;

public class ResponseObj {

    public static <T> ResponseEntity<ResponseDto<T>> success(int statusCode,String message,T data){

        ResponseDto<T> obj = new ResponseDto<>(message, data);

        return ResponseEntity.status(statusCode).body(obj);

    }

        public static <T> ResponseEntity<ResponseDto<T>> error(int statusCode,String message){

            ResponseDto<T> obj = new ResponseDto<>(message, null);

            return ResponseEntity.status(statusCode).body(obj);
        }

    }



