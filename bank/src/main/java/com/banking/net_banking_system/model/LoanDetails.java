package com.banking.net_banking_system.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "LoanDetails")
public class LoanDetails {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId;

    private String loanType;
    
    @Column(precision = 38, scale = 2)
    private BigDecimal loanAmount;

    @Column(precision = 38, scale = 2)
    private BigDecimal monthlyEmiAmount;

    @Column(precision = 38, scale = 2)
    private BigDecimal totalRepaymentAmount;

    @Column(precision = 38, scale = 2)
    private BigDecimal totalAmountPaid = BigDecimal.ZERO;

    @Column(precision = 38, scale = 2)
    private BigDecimal totalAmountRemaining;
    
    private Integer tenureMonths;
    
    @Column(precision = 38, scale = 2)
    private BigDecimal annualIncome;
    private BigDecimal monthlyIncome;

    public BigDecimal getMonthlyIncome() {
		return monthlyIncome;
	}

	public void setMonthlyIncome(BigDecimal monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	@Column(precision = 38, scale = 2)
    private BigDecimal ongoingDebt;

    private Double interestRate = 12.0;
    
    private Integer creditScore;
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Column(nullable = false)
    private String status; // APPROVED, REJECTED, CLOSED

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"accountDetails", "loans", "password"})
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public String getLoanType() {
		return loanType;
	}

	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}

	public BigDecimal getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(BigDecimal amount) {
		this.loanAmount = amount;
	}

	public Integer getTenureMonths() {
		return tenureMonths;
	}

	public void setTenureMonths(Integer tenureMonths) {
		this.tenureMonths = tenureMonths;
	}

	public BigDecimal getAnnualIncome() {
		return annualIncome;
	}

	public void setAnnualIncome(BigDecimal annualIncome) {
		this.annualIncome = annualIncome;
	}

	public Integer getCreditScore() {
		return creditScore;
	}

	public void setCreditScore(Integer creditScore) {
		this.creditScore = creditScore;
	}

	public BigDecimal getOngoingDebt() {
		return ongoingDebt;
	}

	public void setOngoingDebt(BigDecimal ongoingDebt) {
		this.ongoingDebt = ongoingDebt;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getMonthlyEmiAmount() {
		return monthlyEmiAmount;
	}

	public void setMonthlyEmiAmount(BigDecimal monthlyEmiAmount) {
		this.monthlyEmiAmount = monthlyEmiAmount;
	}

	public BigDecimal getTotalRepaymentAmount() {
		return totalRepaymentAmount;
	}

	public void setTotalRepaymentAmount(BigDecimal totalRepaymentAmount) {
		this.totalRepaymentAmount = totalRepaymentAmount;
	}

	public BigDecimal getTotalAmountPaid() {
		return totalAmountPaid;
	}

	public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}

	public BigDecimal getTotalAmountRemaining() {
		return totalAmountRemaining;
	}

	public void setTotalAmountRemaining(BigDecimal totalAmountRemaining) {
		this.totalAmountRemaining = totalAmountRemaining;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}
	
}
