package com.centeral_hub.centeral_hub.utils;

import com.centeral_hub.centeral_hub.model.SettlementLogsModel;
import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

public class SettlementLogHelperClass {

    @Autowired
    private SettlementRepository settlementRepository;

    public void settlementLogHelperMethod(UUID correlationId, String bankServiceName, SettlementLogsModel.Direction direction, String responseCode, String rawPayload, int retryCount) {

        SettlementLogsModel settlementLogsModel = new SettlementLogsModel();

        settlementLogsModel.setCorrelationId(correlationId);
        settlementLogsModel.setBankServiceName(bankServiceName);
        settlementLogsModel.setDirection(direction);
        settlementLogsModel.setResponseCode(responseCode);
        settlementLogsModel.setRawPayload(rawPayload);
        settlementLogsModel.setRetryCount(retryCount);

        settlementRepository.save(settlementLogsModel);
    }

}






