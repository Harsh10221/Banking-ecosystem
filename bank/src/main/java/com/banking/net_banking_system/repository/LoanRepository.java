package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.LoanDetailsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<LoanDetailsModel,Long> {

}
