package com.centeral_hub.centeral_hub.repository;

import com.centeral_hub.centeral_hub.model.MasterBalance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface MasterBalanceRepository extends JpaRepository<MasterBalance, Long> {

//    Lock an row so one user can do transaction at a time as no 2 users create
//            issue in balance with negative or unexpected amount

    //    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT a FROM Master_balance a WHERE a.id = :id")
//    Optional<MasterBalance> findByIdWithLock(@Param("id") Long id);


    // It will return 1 if true 0 if false, null if the input is null
    @Modifying
    @Query("UPDATE MasterBalance m SET m.balance = m.balance - :amount WHERE m.bankId = :bankId AND m.balance >= :amount")
    int updateMasterBalance(String bankId, BigDecimal amount);

}
