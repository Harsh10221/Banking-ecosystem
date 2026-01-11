package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.AccountDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountDetails,Long> {

    Optional<AccountDetails> findByAccountNumber(String accountNumber);


    //with transaction, the db will lock the row till the math is done so another query cannot change the row again

//    Read-Modify-Save, Hibernate way , when there is complex logic and the query involves some work in the java program aswell.

//    Performance is critical: You are updating thousands of rows at once. Fetching all those into Java memory just to change one value is a massive waste of resources.

    @Modifying
    @Transactional
    @Query("UPDATE AccountDetails a" +
            " SET a.balance = a.balance - :balance" +
            " WHERE a.accountNumber = :accountNumber AND a.balance >= :balance")
    int substractBalance(String accountNumber,Long balance);



}
