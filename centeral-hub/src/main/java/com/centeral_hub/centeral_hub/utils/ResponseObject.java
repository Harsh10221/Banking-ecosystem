package com.centeral_hub.centeral_hub.utils;

import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
public class ResponseObject<T> {

    private int statusCode;
    private String msg;
    private T data;

    public static <T> ResponseEntity<ResponseObject<T>> createResponseObj(int statusCode,String msg,T data){
    ResponseObject<T> res = new ResponseObject<>();

    res.setStatusCode(statusCode);
    res.setMsg(msg);
    res.setData(data);

    return ResponseEntity.status(statusCode).body(res);

    };
}
