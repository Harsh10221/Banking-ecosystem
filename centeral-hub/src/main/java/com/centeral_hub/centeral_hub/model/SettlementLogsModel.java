package com.centeral_hub.centeral_hub.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
public class SettlementLogsModel {

    private enum Direction {
        INBOUND,OUTBOUND
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long settlementId;

    @Column(nullable = false)
    private String correlationId;

    @Column(nullable = false)
    private String bankServiceName;

    @Column(nullable = false)
    private Direction direction;

    @Column(nullable = false)
    private String responseCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload",columnDefinition="jsonb")
    private String rawPayload;

    private Integer retryCount;

}






