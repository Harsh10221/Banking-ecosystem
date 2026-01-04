package com.banking.net_banking_system.repository;

import com.banking.net_banking_system.model.EmiSchedule;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {
	
	List<EmiSchedule> findByStatusAndDueDateBefore(String status, LocalDate date);
}