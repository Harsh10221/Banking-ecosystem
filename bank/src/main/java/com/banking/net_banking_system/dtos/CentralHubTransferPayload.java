package com.banking.net_banking_system.dtos;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
public record CentralHubTransferPayload(
        String senderAccountNumber,
        BigDecimal amount,
        String receiverAccountNumber,
        String receiverBank,
        UUID userRequestKey,
        TokenDetails token
) {
        public CentralHubTransferPayload {
                if (senderAccountNumber == null || senderAccountNumber.isBlank()) {
                        throw new IllegalArgumentException("Sender account number is required");
                }

                if (amount == null) {
                        throw new IllegalArgumentException("Amount is required");
                }
                if (amount.compareTo(new BigDecimal("0.01")) < 0) {
                        throw new IllegalArgumentException("Amount must be at least 0.01");
                }

                if (receiverAccountNumber == null || receiverAccountNumber.isBlank()) {
                        throw new IllegalArgumentException("Receiver account number is required");
                }
                if (receiverBank == null || receiverBank.isBlank()) {
                        throw new IllegalArgumentException("Receiver bank code is required");
                }

                if (userRequestKey == null) {
                        throw new IllegalArgumentException("User request key is required");
                }
                if (token == null) {
                        throw new IllegalArgumentException("Token details are required");
                }
        }

        public record TokenDetails(
                String issuer,
                String token
        ){
                public TokenDetails {
                        if (issuer == null || issuer.isBlank()) {
                                throw new IllegalArgumentException("Issuer is required");
                        }
                        if (token == null || token.isBlank()) {
                                throw new IllegalArgumentException("Token value is required");
                        }
                }
        }
}