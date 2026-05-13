package com.banking.net_banking_system.dtos;

import com.banking.net_banking_system.model.TransferModel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionDataWebsocket {
    private String sourceBank;
    private Long id;
    private String destinationBank;
    private BigDecimal amount;
    private TransferModel.status transferStatus;
    private String createdAt;

    public TransactionDataWebsocket(String sourceBank,Long id, String destinationBank, BigDecimal amount, String transferStatus, Instant createdAt) {
        this.sourceBank = sourceBank;
        this.id = id;
        this.destinationBank = destinationBank;
        this.amount = amount;
        this.transferStatus = TransferModel.status.valueOf(transferStatus);
        this.createdAt = createdAt.toString();


    }
}
