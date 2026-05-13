package com.banking.net_banking_system.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Data
@Entity
@Table(name = "Transaction")
public class TransactionModel {

    public enum TransactionType {DEBIT, CREDIT, COMPENSATION}

    public enum Status {SUCCESS, FAILED}


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @NotNull(message = "Correlation Id is required")
    private UUID correlationId;

    @NotNull
    @DecimalMin(value = "1", message = "Amount should be greater than 1")
    private BigDecimal amount;

    @NotBlank(message = "From is required")
    private String sender;

    private String receiver;

    @Enumerated(EnumType.STRING)
    private Status transactionStatus;

    private String senderBank;


}
