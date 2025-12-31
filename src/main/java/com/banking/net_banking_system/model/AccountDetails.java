package com.banking.net_banking_system.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="AccountDetails")
public class AccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountNo;

    @OneToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private enum accountType{SAVING,CURRENT};
    private Long accountBalance;
    private enum status{ACTIVE,FROZE,CLOSED};
    private Instant createdAt;

    @Column(length = 3)
    private String currency;

}
