package com.banking.net_banking_system.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;


@Entity
@Data
@Table(name = "Transaction")
public class Transaction {



    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public void setType(Type input ) {
        this.transactionType = input;
    }

    public Type getType() {
        return transactionType;
    }

//    public void setAmount(Long amount) {
//        this.amount = amount;
//    }


    public Instant getCreatedAT() {
        return createdAt;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false )
    private User user;

    public enum Type{DEPOSIT,WITHDRAW,AUTO_PAY};
    @Enumerated(EnumType.STRING)
    private Type transactionType;
    private enum status{PENDING, APPROVED, REJECTED, EXPIRED};
    private Instant createdAt ;
    private Long amount;
    private String destAccountNumber;



}
