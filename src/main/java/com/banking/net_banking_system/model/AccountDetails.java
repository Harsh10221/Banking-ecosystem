package com.banking.net_banking_system.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="AccountDetails")
public class AccountDetails {
    public void setAccountNo(Long accountNo) {
        this.accountNo = accountNo;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAccountBalance(Long accountBalance) {
        this.accountBalance = accountBalance;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getAccountNo() {
        return accountNo;
    }

    public User getUser() {
        return user;
    }

    public Long getAccountBalance() {
        return accountBalance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getCurrency() {
        return currency;
    }

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
