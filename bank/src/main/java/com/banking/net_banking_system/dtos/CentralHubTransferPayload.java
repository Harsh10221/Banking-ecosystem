package com.banking.net_banking_system.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
public record CentralHubTransferPayload(
        String bankName,
        String token,
        UUID userRequestKey
) {
        public CentralHubTransferPayload {
                if (bankName == null || bankName.isBlank()) {
                        throw new IllegalArgumentException("Receiver account number is required");
                }
                if (token == null) {
                        throw new IllegalArgumentException("Token details are required");
                }
        }

}