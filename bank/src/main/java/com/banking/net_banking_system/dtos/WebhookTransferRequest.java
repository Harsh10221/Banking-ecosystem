package com.banking.net_banking_system.dtos;

import com.banking.net_banking_system.model.TransferModel;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WebhookTransferRequest(

        @NotNull(message = "CorrelationId is required")
        UUID correlationId,
        @NotNull(message = "Transfer stastus requeired")
        String transferStatus,
        String errorMsg
) {
}
