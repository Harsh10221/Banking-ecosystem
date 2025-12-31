package com.banking.net_banking_system.model;


import jakarta.persistence.*;
import jdk.jshell.Snippet;

import java.time.Instant;

@Entity
@Table(name = "Transaction")
public class Transaction {
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedAT(Instant createdAT) {
        this.createdAT = createdAT;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public Instant getCreatedAT() {
        return createdAT;
    }

    public Long getAmount() {
        return amount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false )
    private User user;

    private enum type{DEPOSIT,WITHDRAW,AUTO_PAY};
    private enum status{PENDING, APPROVED, REJECTED, EXPIRED};
    private Instant createdAT;
    private Long amount;



}
