package com.banking.net_banking_system.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "LoanDetails")
public class LoanDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private Long userId;

    private enum loanType{PERSONAL,HOME,CAR,EDUCATION};
    private Long amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private enum status{PENDING,APPLIED,REJECTED,PAID};
    private Instant createdAt;




}
