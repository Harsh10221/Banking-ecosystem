package com.banking.net_banking_system.dtos;

import lombok.Data;

@Data
public class WebsocketTransaction<T> {

    private String type;
    private T data;

    public WebsocketTransaction(String type, T data) {
        this.type = type;
        this.data = data;
    }


}