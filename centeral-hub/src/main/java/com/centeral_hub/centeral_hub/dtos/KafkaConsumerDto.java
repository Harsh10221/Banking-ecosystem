package com.centeral_hub.centeral_hub.utils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
public class KafkaConsumerDto {

        @NotBlank(message = "Sender account required")
        private String senderAccountNumber;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1", message = "Amount must be greater than 1")
        private BigDecimal amount;

        private String type;

        @NotBlank(message = "Receiver account number required")
        private String receiverAccountNumber;

        @NotBlank(message = "Receiver bank required")
        private String receiverBank;

        @NotBlank(message = "User request key required")
        private UUID userRequestKey;

        private UUID correlationId;

        @NotNull(message = "Token required")
        @Valid
        private TokenDetails token;


        public KafkaConsumerDto() {
        }


        @Setter
        @Getter
        public static class TokenDetails {

                @NotBlank(message = "Issuer required")
                private String issuer;

                @NotBlank(message = "Token required")
                private String token;

                public TokenDetails() {
                }

        }
}