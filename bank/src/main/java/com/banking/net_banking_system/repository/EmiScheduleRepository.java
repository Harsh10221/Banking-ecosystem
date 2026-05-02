package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.EmiScheduleModel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiScheduleModel, Long> {
	
	List<EmiScheduleModel> findByStatusAndDueDateBefore(String status, LocalDate date);
	List<EmiScheduleModel> findByLoan_LoanIdOrderByDueDateAsc(Long loanId);
}