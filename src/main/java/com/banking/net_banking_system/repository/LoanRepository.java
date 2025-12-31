package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.LoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanDetails,Long> {

}
