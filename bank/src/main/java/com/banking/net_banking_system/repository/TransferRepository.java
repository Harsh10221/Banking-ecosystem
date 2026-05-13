package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.dtos.TransactionDataWebsocket;
import com.banking.net_banking_system.dtos.WebsocketDashBoardMetrics;
import com.banking.net_banking_system.model.TransferModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TransferRepository extends JpaRepository<TransferModel, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE TransferModel t " +
            "SET t.transferStatus = :transferStatus, t.errorMsg = :errorMsg " +
            " WHERE t.userRequestKey = :userRequestKey")
    void updateTransactionStatus(TransferModel.status transferStatus, String errorMsg, UUID userRequestKey);

    Optional<TransferModel> findByCorrelationId(UUID correlationId);

    @Query(value = "SELECT " +
            "t.source_bank AS sourceBank, " +
            "t.transfer_id AS id, " +
            "t.destination_bank AS destinationBank, " +
            "t.amount AS amount, " +
            "t.transfer_status AS transferStatus, " +
            "t.created_at AS createdAt " +
            "FROM transfer t " +
            "ORDER BY t.created_at DESC " +
            "LIMIT 6",
            nativeQuery = true)
    List<TransactionDataWebsocket> getTransactions();

    @Query(value = "SELECT COUNT(t.id) AS totalCount ,t.transferStatus AS transferStatus" +
            " FROM TransferModel t " +
            " GROUP BY t.transferStatus")
    List<WebsocketDashBoardMetrics> getDashBoardMetric();


}
