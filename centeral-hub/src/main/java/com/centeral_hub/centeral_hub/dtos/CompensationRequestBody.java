package com.centeral_hub.centeral_hub.dtos;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record CompensationRequestBody(



        String senderAccountNumber,

        String receiverAccountNumber,

        String transactionType,

        String senderBank,

        BigDecimal amount,

        UUID correlationId

) {
    public CompensationRequestBody {
        if (senderAccountNumber == null || senderAccountNumber.isBlank()) {
            throw new IllegalArgumentException("Sender account number cannot be blank or null");
        }
        if (receiverAccountNumber == null || receiverAccountNumber.isBlank()) {
            throw new IllegalArgumentException("Sender account number cannot be blank or null");
        }

        if (senderBank == null || senderBank.isBlank()) {
            throw new IllegalArgumentException("Bank name cannot be blank or null");
        }

        if (transactionType == null || transactionType.isBlank()) {
            throw new IllegalArgumentException("Compensation type cannot be blank or null");
        }

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero for compensation");
        }

        Objects.requireNonNull(correlationId, "User request key (UUID) cannot be null");

    }
}
