package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.AccountDetailsModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountDetailsModel,Long> {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Optional<AccountDetailsModel> findByAccountNumber(String accountNumber);

    @Modifying
    @Transactional
    @Query("UPDATE AccountDetailsModel a" +
            " SET a.balance = a.balance - :balance" +
            " WHERE a.accountNumber = :accountNumber AND a.balance >= :balance")
    int debitBalance(String accountNumber,BigDecimal balance);

    @Modifying
    @Transactional
    @Query("UPDATE AccountDetailsModel a" +
            " SET a.balance = a.balance +  :amount" +
            " WHERE a.accountNumber = :accountNumber")
    int creditAmount(String accountNumber,BigDecimal amount);

    boolean existsByAccountNumberAndBalanceGreaterThanEqual(String accountNumber, BigDecimal amount);


}
