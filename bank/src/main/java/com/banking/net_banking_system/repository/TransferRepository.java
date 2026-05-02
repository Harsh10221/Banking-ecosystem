package com.banking.net_banking_system.repository;

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
            "SET t.transferStatus = :transferStatus, t.errorMsg = :errorMsg "  +
            " WHERE t.userRequestKey = :userRequestKey")
    void updateTransactionStatus(TransferModel.status transferStatus, String errorMsg, UUID userRequestKey);

    Optional<TransferModel> findByCorrelationId(UUID correlationId);




}
