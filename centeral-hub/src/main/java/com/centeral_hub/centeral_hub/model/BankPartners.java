package com.centeral_hub.centeral_hub.model;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class BankPartners {

    public enum Status {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankId;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String bankBaseUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "varchar(225)")
    private Status status = Status.INACTIVE;

    @Column(columnDefinition = "varchar(1000)")
    private String bankPublicKey;

}
