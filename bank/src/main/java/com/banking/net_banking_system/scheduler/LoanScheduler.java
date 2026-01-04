package com.banking.net_banking_system.scheduler;

import com.banking.net_banking_system.model.EmiSchedule;
import com.banking.net_banking_system.model.LoanDetails;
import com.banking.net_banking_system.repository.EmiScheduleRepository;
import com.banking.net_banking_system.repository.LoanRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class LoanScheduler {

    @Autowired
    private EmiScheduleRepository emiScheduleRepository;
    
    @Autowired
    private LoanRepository loanRepository;

    // Runs every day at 12:00 AM
    @Scheduled(cron = "0 0 0 * * ?") 
    public void applyOverduePenalties() {
        LocalDate today = LocalDate.now();
        
        // Find unpaid EMIs where due date has passed
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findByStatusAndDueDateBefore("UNPAID", today);

     // Inside LoanScheduler.java
        for (EmiSchedule emi : overdueEmis) {
            emi.setStatus("OVERDUE");

            // 1. Calculate the 2% penalty
            BigDecimal penalty = emi.getEmiAmount().multiply(new BigDecimal("0.02"));
            emi.setPenaltyAmount(penalty);

            // 2. Update the Loan's total amounts to include this new fee
            LoanDetails loan = emi.getLoan();
            loan.setTotalRepaymentAmount(loan.getTotalRepaymentAmount().add(penalty));
            loan.setTotalAmountRemaining(loan.getTotalAmountRemaining().add(penalty));

            // 3. Save both
            emiScheduleRepository.save(emi);
            loanRepository.save(loan);
        }
    }
}