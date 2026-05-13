package com.banking.net_banking_system.dtos;

import com.banking.net_banking_system.model.TransferModel;

public record WebsocketDashBoardMetrics(
        Long totalCount,
        TransferModel.status transferStatus

) {
}
