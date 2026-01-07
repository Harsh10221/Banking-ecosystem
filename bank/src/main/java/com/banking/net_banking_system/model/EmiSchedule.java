package com.banking.net_banking_system.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "emi_schedule")
public class EmiSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    @JsonIgnore
    private LoanDetails loan;

    private LocalDate dueDate;
    
    @Column(precision = 38, scale = 2)
    private BigDecimal emiAmount;

    @Column(precision = 38, scale = 2)
    private BigDecimal penaltyAmount = BigDecimal.ZERO;

    private String status; // UNPAID, PAID, OVERDUE

    
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LoanDetails getLoan() { return loan; }
    public void setLoan(LoanDetails loan) { this.loan = loan; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public BigDecimal getEmiAmount() { return emiAmount; }
    public void setEmiAmount(BigDecimal emiAmount) { this.emiAmount = emiAmount; }
    public BigDecimal getPenaltyAmount() { return penaltyAmount; }
    public void setPenaltyAmount(BigDecimal penaltyAmount) { this.penaltyAmount = penaltyAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}