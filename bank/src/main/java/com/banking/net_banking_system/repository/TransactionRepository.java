package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.Transaction;
import com.banking.net_banking_system.model.User;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop5ByUserOrderByCreatedAtDesc(User user);

    List<Transaction> findByUserOrderByCreatedAtDesc(User user);

    @Transactional
    @Modifying
    @Query("UPDATE Transaction t " +
            "SET t.transactionStatus = :transactionStatus, t.errorMsg = :errorMsg "  +
            " WHERE t.transactionStatus = PENDING AND t.user = :user AND t.destinationAccountNumber = :destinationAccountNumber AND t.destinationBank = :destinationBank AND t.amount = :amount ")
    void updateTransactionStatus(Transaction.status transactionStatus,User user, Long destinationAccountNumber, String destinationBank, BigDecimal amount,String errorMsg);

}

