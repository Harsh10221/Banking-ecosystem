package com.banking.net_banking_system.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "LoanDetails")
public class LoanDetails {
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public Long getAmount() {
        return amount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private enum loanType{PERSONAL,HOME,CAR,EDUCATION};
    private Long amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private enum status{PENDING,APPLIED,REJECTED,PAID};
    private Instant createdAt;




}
