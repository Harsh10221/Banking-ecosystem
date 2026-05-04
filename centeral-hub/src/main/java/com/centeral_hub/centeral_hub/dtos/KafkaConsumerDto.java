package com.centeral_hub.centeral_hub.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@ToString
public class KafkaConsumerDto {

        @NotBlank(message = "Sender account required")
        private String senderAccountNumber;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1", message = "Amount must be greater than 1")
        private BigDecimal amount;

        @NotBlank(message = "Receiver account number required")
        private String receiverAccountNumber;

        @NotBlank(message = "Receiver bank required")
        private String receiverBank;

        @NotBlank(message = "Sender bank required")
        private String senderBank;

        private UUID userRequestKey;

        private UUID correlationId;




}