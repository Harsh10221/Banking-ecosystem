package com.centeral_hub.centeral_hub.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
public class BankMasterBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankId;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private BigDecimal reservedBalance;

    @Column(nullable = false)
    private String currency;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private Timestamp last_updated_at;

//    private String ;



}



