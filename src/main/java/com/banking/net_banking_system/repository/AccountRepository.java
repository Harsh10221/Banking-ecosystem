package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.AccountDetails;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountDetails,Long> {
	
}
