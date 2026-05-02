package com.centeral_hub.centeral_hub.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record WithdrawDataDtoConsumer(

        String accountNumber ,

        String type,

        BigDecimal amount,

        UUID correlationId

) {
        public WithdrawDataDtoConsumer {
                if (accountNumber == null || accountNumber.isBlank()) {
                        throw new IllegalArgumentException("Account number is required and cannot be blank");
                }

                if (type == null || type.isBlank()) {
                        throw new IllegalArgumentException("Withdrawal type is required");
                }

                if (amount == null) {
                        throw new IllegalArgumentException("Amount is required");
                }
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
                }

                Objects.requireNonNull(correlationId, "Correlation Id is required");

        }
}
