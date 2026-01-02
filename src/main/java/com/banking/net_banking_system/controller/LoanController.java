package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.model.LoanDetails;
import com.banking.net_banking_system.repository.LoanRepository;
import com.banking.net_banking_system.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;
    
    @Autowired
    private LoanRepository loanRepository;

    @PostMapping("/apply")
    public String applyForLoan(@RequestBody LoanDetails loan) {
        try {
            return loanService.processLoanApplication(loan);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
    
    @GetMapping("/status/{loanId}")
    public LoanDetails getLoanStatus(@PathVariable Long loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
    }
    
    @PostMapping("/pay-emi/{emiId}")
    public String payEmi(@PathVariable Long emiId) {
        try {
            return loanService.payEmi(emiId);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}