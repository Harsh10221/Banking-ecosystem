package com.banking.net_banking_system.model;


import jakarta.persistence.*;
import jdk.jshell.Snippet;

import java.time.Instant;

@Entity
@Table(name = "Transaction")
public class Transaction {

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
