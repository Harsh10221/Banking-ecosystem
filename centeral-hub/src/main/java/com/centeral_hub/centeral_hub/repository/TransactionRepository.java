package com.centeral_hub.centeral_hub.repository;

import com.centeral_hub.centeral_hub.model.TransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel,Long> {

    @Modifying
    @Transactional
    @Query("UPDATE TransactionModel t SET t.status = :status, t.errorMsg = :errorMsg WHERE t.transactionId = :transactionId ")
    void changeStatusAndAddErrorMsg( Long transactionId, TransactionModel.Status status,String errorMsg);

    @Modifying
    @Transactional
    @Query("UPDATE TransactionModel t SET t.status = :status WHERE t.correlationId = :correlationId ")
    void updateStatus(TransactionModel.Status status, UUID correlationId );


    Optional<TransactionModel> findByCorrelationId(UUID correlationId);


}
