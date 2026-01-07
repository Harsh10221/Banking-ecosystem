package com.centeral_hub.centeral_hub.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
public class TransactionModel {

    private enum Status{
        PENDING,VALIDATED,SUCCES,FAILED,REVERSED;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String senderAccountNumber;

    @Column(nullable = false)
    private String receiverAccountNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(255) default 'PENDING'")
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;



}
