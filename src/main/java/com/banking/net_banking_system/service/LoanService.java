package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.LoanDetails;
import com.banking.net_banking_system.repository.LoanRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    public String processLoanApplication(LoanDetails loan) {
        // Reject if credit score<750
        if (loan.getCreditScore() < 750) {
            loan.setStatus("REJECTED");
            loanRepository.save(loan);
            return "REJECTED: Credit score too low.";
        }
        
        // loan amt > 30% of Annual income than reject
        BigDecimal annualIncome = loan.getMonthlyIncome().multiply(new BigDecimal(12));
        BigDecimal affordabilityLimit = annualIncome.multiply(new BigDecimal("0.30"));

        if (loan.getLoanAmount().compareTo(affordabilityLimit) > 0) {
            loan.setStatus("REJECTED");
            loanRepository.save(loan);
            return "REJECTED: Loan amount exceeds 30% of annual income ($" + affordabilityLimit + ").";
        }

        // Approve score>750
        loan.setStatus("APPROVED");
        
        // Loan start/end date
        loan.setStartDate(LocalDate.now());
        if (loan.getTenureMonths() != null) {
            loan.setEndDate(LocalDate.now().plusMonths(loan.getTenureMonths()));
        }

        loanRepository.save(loan);
        return "APPROVED";
    }
}