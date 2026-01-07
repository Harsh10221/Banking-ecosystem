package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.model.EmiSchedule;
import com.banking.net_banking_system.model.LoanDetails;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.EmiScheduleRepository;
import com.banking.net_banking_system.repository.LoanRepository;
import com.banking.net_banking_system.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private EmiScheduleRepository emiScheduleRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String processLoanApplication(LoanDetails loan) {
        // 0. FETCH PERSISTENT USER: Required to access AccountDetails
        if (loan.getUser() == null || loan.getUser().getId() == null) {
            return "ERROR: User ID is missing in the request.";
        }
        
        Long userId = loan.getUser().getId();
        User fullUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Attach the fully loaded user to the loan
        loan.setUser(fullUser);

        // 1. Credit Score Gate: Reject if below 750
        if (loan.getCreditScore() == null || loan.getCreditScore() < 750) {
            loan.setStatus("REJECTED");
            loanRepository.save(loan);
            return "REJECTED: Credit score too low.";
        }

        // 2. Reject if ongoing debt > 50% of annual income
        BigDecimal annualIncome = loan.getAnnualIncome();
        BigDecimal dtiLimit = annualIncome.multiply(new BigDecimal("0.50"));
        if (loan.getOngoingDebt().compareTo(dtiLimit) > 0) {
            loan.setStatus("REJECTED");
            loanRepository.save(loan);
            return "REJECTED: Ongoing debt exceeds 50% of annual income.";
        }

        // 3. 30% Affordability : Reject if loan amount > 30% of annual income
        BigDecimal affordabilityLimit = annualIncome.multiply(new BigDecimal("0.30"));
        if (loan.getLoanAmount().compareTo(affordabilityLimit) > 0) {
            loan.setStatus("REJECTED");
            loanRepository.save(loan);
            return "REJECTED: Loan amount exceeds 30% of annual income (Limit: $" + affordabilityLimit + ").";
        }

        // 4. APPROVED: Calculate EMI, Total Repayment, and Dates
        calculateEmiAndRepayment(loan);
        loan.setStatus("APPROVED");
        loan.setTotalAmountRemaining(loan.getTotalRepaymentAmount());
        loan.setStartDate(LocalDate.now());
        loan.setEndDate(LocalDate.now().plusMonths(loan.getTenureMonths()));

        // 5. CREDIT AMOUNT TO ACCOUNT: Update user's bank balance
        if (fullUser.getAccountDetails() != null) {
            AccountDetails account = fullUser.getAccountDetails();
            account.creditBalance(loan.getLoanAmount());
        } else {
            return "ERROR: No linked bank account found for user ID: " + userId;
        }

        // 6. SAVE & GENERATE SCHEDULE: Create the monthly Emi repayment list
        LoanDetails savedLoan = loanRepository.save(loan);
        generateEmiSchedule(savedLoan);

        return "APPROVED: Fund credited and EMI schedule generated.";
    }

    private void calculateEmiAndRepayment(LoanDetails loan) {
        // Calculation: Total Interest = Principal * Rate * (Tenure/12)
        BigDecimal interestRateDecimal = new BigDecimal(loan.getInterestRate()).divide(new BigDecimal(100));
        BigDecimal timeInYears = new BigDecimal(loan.getTenureMonths()).divide(new BigDecimal(12), 2, RoundingMode.HALF_UP);
        
        BigDecimal totalInterest = loan.getLoanAmount()
                .multiply(interestRateDecimal)
                .multiply(timeInYears);
        
        // Total Repayment = Principal + Interest
        BigDecimal totalRepayment = loan.getLoanAmount().add(totalInterest);
        loan.setTotalRepaymentAmount(totalRepayment);
        
        // Monthly EMI = Total Repayment / Tenure
        BigDecimal monthlyEmi = totalRepayment.divide(new BigDecimal(loan.getTenureMonths()), 2, RoundingMode.HALF_UP);
        loan.setMonthlyEmiAmount(monthlyEmi);
    }

    private void generateEmiSchedule(LoanDetails loan) {
        BigDecimal totalScheduled = BigDecimal.ZERO;
        
        // Create a row for each month in the tenure
        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            EmiSchedule emi = new EmiSchedule();
            emi.setLoan(loan);
            emi.setDueDate(LocalDate.now().plusMonths(i)); 
            emi.setStatus("UNPAID");

            // Adjust the LAST EMI to handle rounding differences
            if (i == loan.getTenureMonths()) {
                // Last EMI = Total Repayment - (Sum of all previous EMIs)
                BigDecimal remainingToSchedule = loan.getTotalRepaymentAmount().subtract(totalScheduled);
                emi.setEmiAmount(remainingToSchedule);
            } else {
                // Normal EMI
                emi.setEmiAmount(loan.getMonthlyEmiAmount());
                totalScheduled = totalScheduled.add(loan.getMonthlyEmiAmount());
            }

            emiScheduleRepository.save(emi);
        }
    }
    
    @Transactional
    public String payEmi(Long emiId) {
        EmiSchedule emi = emiScheduleRepository.findById(emiId)
                .orElseThrow(() -> new RuntimeException("EMI not found"));

        if (emi.getStatus().equals("PAID")) return "EMI already paid.";

        LoanDetails loan = emi.getLoan();
        BigDecimal totalToPay = emi.getEmiAmount().add(emi.getPenaltyAmount());

        // 1. Check if user has enough balance
        AccountDetails account = loan.getUser().getAccountDetails();
        if (account.getBalance().compareTo(totalToPay) < 0) {
            return "ERROR: Insufficient balance to pay EMI.";
        }

        // 2. Deduct from account and update Loan tracking
        account.setBalance(account.getBalance().subtract(totalToPay));
        loan.setTotalAmountPaid(loan.getTotalAmountPaid().add(totalToPay));
        
        // Update remaining amount
        BigDecimal newRemaining = loan.getTotalAmountRemaining().subtract(totalToPay);
        loan.setTotalAmountRemaining(newRemaining);

        // 3. Mark EMI as PAID
        emi.setStatus("PAID");

        // 4. Close loan if remaining amount is zero OR very small
        if (newRemaining.compareTo(new BigDecimal("1.00")) < 0) {
            loan.setStatus("CLOSED");
            loan.setTotalAmountRemaining(BigDecimal.ZERO); // Clean up the 0.04
        }

        loanRepository.save(loan);
        emiScheduleRepository.save(emi);
        return "SUCCESS: EMI Paid ($" + totalToPay + ")";
    }
    
    // Get EMI schedule for a particular loan
    public List<EmiSchedule> getLoanEmiSchedule(Long loanId) {
        return emiScheduleRepository.findByLoan_LoanIdOrderByDueDateAsc(loanId);
    }
}