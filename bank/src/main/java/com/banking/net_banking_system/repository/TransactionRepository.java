package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.Transaction;
import com.banking.net_banking_system.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
	
	List<Transaction> findTop5ByUserOrderByCreatedAtDesc(User user);
	
	List<Transaction> findByUserOrderByCreatedAtDesc(User user);
	
}
