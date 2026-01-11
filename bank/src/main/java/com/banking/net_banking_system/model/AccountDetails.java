package com.banking.net_banking_system.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "account_details")
public class AccountDetails {

	public enum AccountStatus{
		ACTIVE,BLOCKED,FROZEN
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String accountType; // SAVINGS or CURRENT

    @Column(nullable = false)
    private BigDecimal balance ;

    @Column(nullable = false)
	@Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE; // ACTIVE or BLOCKED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
	@ToString.Exclude
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
//        if (this.status == null) this.status = "ACTIVE";
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