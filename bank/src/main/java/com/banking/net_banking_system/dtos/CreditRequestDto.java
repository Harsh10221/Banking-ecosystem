package com.banking.net_banking_system.dtos;

import com.banking.net_banking_system.model.TransactionModel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditRequestDto(
        @NotBlank(message = "Sender account number cannot be blank or null")
        String senderAccountNumber,

        @NotNull(message = "Amount cannot be blank")
        @DecimalMin(value = "1.00", inclusive = false, message = "Amount must be greater than 1")
        BigDecimal amount,

        TransactionModel.TransactionType transactionType,

        @NotBlank(message = "Receiver account number cannot be blank or null")
        String receiverAccountNumber,

        @NotBlank(message = "Sender bank cannot be blank or null")
        String senderBank,

        @NotNull(message = "Correlation Id cannot be null")
        UUID correlationId

) {
}
