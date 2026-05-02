package com.banking.net_banking_system.utils;

import lombok.Data;

@Data
public class  ResponseDto<T> {

    private String message;
    private T data;

    ResponseDto(String message,T data){
        this.message = message;
        this.data = data;
    }

}
