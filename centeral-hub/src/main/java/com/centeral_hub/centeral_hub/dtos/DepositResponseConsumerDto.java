package com.centeral_hub.centeral_hub.dtos;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DepositResponseConsumerDto<T> {

    private String message;
    private T data;

   //Jackson MUST have a completely empty constructor to start with
    public DepositResponseConsumerDto() {


    }

    public void validate() {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be blank or null");
        }
        Objects.requireNonNull(data, "Response data payload cannot be null");
    }
}