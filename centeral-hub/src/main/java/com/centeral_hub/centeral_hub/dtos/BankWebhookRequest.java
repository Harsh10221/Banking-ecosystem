package com.centeral_hub.centeral_hub.dtos;
import lombok.Data;

import java.util.Objects;
import java.util.UUID;


@Data
public class BankWebhookRequest {

    public enum TransactionStatusWebhook {
        REJECTED, SUCCESS
    }

    private UUID correlationId;
    private BankWebhookRequest.TransactionStatusWebhook transferStatus;
    private String errorMsg;

    public  BankWebhookRequest(UUID correlationId,BankWebhookRequest.TransactionStatusWebhook transferStatus,String errorMsg){

        this.correlationId = Objects.requireNonNull(correlationId,"CorrelationId is required");
        this.transferStatus = Objects.requireNonNull(transferStatus,"Transfer status is required");
        this.errorMsg = errorMsg;
    }



}
