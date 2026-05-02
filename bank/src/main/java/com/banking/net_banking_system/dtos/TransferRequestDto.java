package com.banking.net_banking_system.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequestDto(
        @NotBlank(message = "Sender account number is required")
        String senderAccountNumber,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1", message = "Transfer amount should be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Receiver account number is required")
        String  receiverAccountNumber,

        @NotBlank(message = "Receiver bank is required")
        String receiverBank


) {
}
