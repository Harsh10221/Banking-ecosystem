package com.centeral_hub.centeral_hub.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.sql.ast.tree.expression.Star;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
public class LegderModel {

    private enum Transactiontype {
        DEBIT,CREDIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long legerId;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "bank")
    private BankMasterBalance bankMasterBalance;

    @Column(nullable = false)
    private Transactiontype transactiontype;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private Timestamp createdAt;

}
