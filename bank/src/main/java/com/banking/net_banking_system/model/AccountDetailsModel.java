package com.banking.net_banking_system.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Negative;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Check;

@Entity
@Data
@Table(name = "account_details")
public class AccountDetailsModel {

	public enum AccountStatus{
		ACTIVE,BLOCKED,FROZEN
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    @Check(constraints = "balance >= 0")
    private BigDecimal balance ;

    @Column(nullable = false)
	@Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
	@ToString.Exclude
    private UserModel user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.balance == null) this.balance = BigDecimal.ZERO;
    }
    
    public void creditBalance(BigDecimal amount) {
        if (this.balance == null) this.balance = BigDecimal.ZERO;
        this.balance = this.balance.add(amount);
    }

	public void setBalance(BigDecimal balance) {
		System.out.println("from set "+balance);
		 this.balance = balance;
	}




    
}