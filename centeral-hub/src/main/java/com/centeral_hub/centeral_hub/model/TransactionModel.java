package com.centeral_hub.centeral_hub.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
public class TransactionModel {

    public enum Status{
        PENDING,VALIDATED, SUCCESS,FAILED,REVERSED,INITIATED;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

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

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;



}
