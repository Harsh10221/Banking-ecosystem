package com.centeral_hub.centeral_hub.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record DepositRequestBodyConsumerDto(

        String senderAccountNumber,
        BigDecimal amount,
        String receiverAccountNumber,
        String senderBank,
        UUID correlationId

) {
    public DepositRequestBodyConsumerDto {
        if (senderAccountNumber == null || senderAccountNumber.isBlank()) {
            throw new IllegalArgumentException("Sender account number cannot be blank or null");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(new BigDecimal("1.00")) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 1.00");
        }
        if (receiverAccountNumber == null || receiverAccountNumber.isBlank()) {
            throw new IllegalArgumentException("Receiver account number cannot be blank or null");
        }
        if (senderBank == null || senderBank.isBlank()) {
            throw new IllegalArgumentException("Sender bank cannot be blank or null");
        }

        Objects.requireNonNull(correlationId, "Correlation Id cannot be null");


    }
}
