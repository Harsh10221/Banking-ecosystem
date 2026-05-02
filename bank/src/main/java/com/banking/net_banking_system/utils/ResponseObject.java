package com.banking.net_banking_system.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Data
public class ResponseObject<T> {

    private int statusCode;
    private String message;
    private T data;

    public static <T> ResponseEntity<ResponseObject<T>> createResponse(int code, String msg, T data, HttpStatus status) {
        ResponseObject<T> res = new ResponseObject<>();
        res.setStatusCode(code);
        res.setMessage(msg);
        res.setData(data);
        return new ResponseEntity<>(res, status);
    }


}
