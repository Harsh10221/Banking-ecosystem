package com.banking.net_banking_system.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;


@Entity
@Data
@Table(name = "Transaction")
public class Transaction {


    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public void setType(Type input) {
        this.transactionType = input;
    }

    public Type getType() {
        return transactionType;
    }

    public Instant getCreatedAT() {
        return createdAt;
    }

    public enum Type {DEBIT,CREDIT,TRANSFER}
    public enum status {PENDING, APPROVED, REJECTED, EXPIRED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private Type transactionType;

    @Enumerated(EnumType.STRING)
    private status transactionStatus = status.PENDING ;

    private Instant createdAt;
    private BigDecimal amount;

    private Long sourceAccountNumber; ///new
    private String sourceBank; ///new
    private Long destinationAccountNumber;
    private String destinationBank;
    private String corelationId;
    private String errorMsg; ///mew



}
