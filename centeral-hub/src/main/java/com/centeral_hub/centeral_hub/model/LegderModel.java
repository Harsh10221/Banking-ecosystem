package com.centeral_hub.centeral_hub.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.UUID;


@Data
@Entity
@Table(name = "ledger_model")
public class LegderModel {

    public enum Transactiontype {
        DEBIT, CREDIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long legerId;

    @Column(name = "correlation_id", columnDefinition = "uuid")
    private UUID correlationId;

    // Fix: Changed to Long to match TransactionModel ID type
    @Column(nullable = false)
    private Long transactionId;

    // Fix: Added missing fields required by TransactionService
    @Column(nullable = false)
    private String senderBank;

    @Column(nullable = false)
    private String receiverBank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Transactiontype transactiontype;
    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Transactiontype transactionType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private String description; 

    // Fix: Use LocalDateTime
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}