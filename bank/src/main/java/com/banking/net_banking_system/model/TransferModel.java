package com.banking.net_banking_system.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "Transfer")
public class TransferModel {



    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }


    public Instant getCreatedAT() {
        return createdAt;
    }

    public enum status {PENDING, APPROVED, REJECTED, EXPIRED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;


    @Enumerated(EnumType.STRING)
    private status transferStatus = status.PENDING ;

    private Instant createdAt;
    private BigDecimal amount;

    private String sourceAccountNumber; ///new
    private String sourceBank; ///new
    private String destinationAccountNumber;
    private String destinationBank;
    private UUID correlationId;
    @Column(columnDefinition = "varchar(1000)")
    private String errorMsg; ///mew

    private UUID userRequestKey;


}
