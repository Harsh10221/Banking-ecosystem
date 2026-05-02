package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.TransactionModel;
import com.banking.net_banking_system.model.UserModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {

    Optional<TransactionModel> findByCorrelationId(UUID correlationId);

    Optional<TransactionModel> findByCorrelationIdAndTransactionType(UUID correlationId,TransactionModel.TransactionType transactionType);

   boolean existsByCorrelationIdAndTransactionStatusAndTransactionType(UUID correlationId, TransactionModel.Status status, TransactionModel.TransactionType type );


}

