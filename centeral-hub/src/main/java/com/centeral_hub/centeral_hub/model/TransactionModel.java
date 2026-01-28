package com.centeral_hub.centeral_hub.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.UUID;

@Data
@Entity
@Table(name = "transaction_model")
public class TransactionModel {

    public enum Status {
        PENDING, 
        VALIDATED, 
        SUCCESS, 
        COMPLETED, // Added missing status
        FAILED, 
        REVERSED, 
        INITIATED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    // Best Practice: Store UUIDs as Strings for easier debugging and cross-DB compatibility
    @Column(name = "correlation_id", nullable = false)
    private UUID correlationId;

    @Column(nullable = false)
    private String senderAccountNumber;

    @Column(nullable = false)
    private String receiverAccountNumber;
    
    @Column(nullable = false)
    private String senderBank;
    
    @Column(nullable = false)
    private String receiverBank;

    @Column(nullable = false)
    private BigDecimal amount;


    private String errorMsg;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(255) ")
    private Status status = Status.INITIATED;

    // Efficient: Use LocalDateTime (standard since Java 8) instead of legacy Timestamp
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}