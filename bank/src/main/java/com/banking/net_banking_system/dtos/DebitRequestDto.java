package com.banking.net_banking_system.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DebitRequestDto(
        @NotBlank(message = "Account number is required")
        String accountNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1",message = "Minimum amount should be greater that 1")
        BigDecimal amount,

        @NotBlank(message = "correlation Id is required")
        UUID correlationId
) {
}
