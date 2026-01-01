package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.model.LoanDetails;
import com.banking.net_banking_system.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/apply")
    public String applyForLoan(@RequestBody LoanDetails loan) {
        try {
            return loanService.processLoanApplication(loan);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}