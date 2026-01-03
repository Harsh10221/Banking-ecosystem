package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.model.Transaction;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.AccountRepository;
import com.banking.net_banking_system.repository.TransactionRepository;
import com.banking.net_banking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Controller
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String depositTransaction(String accountNumber, String type, Long amount, Long userId) {
        Transaction newTransaction = new Transaction();

        if (accountNumber == null || !type.equals("Deposit") || amount == null) {
            return "Account No or amount are required";
        }

        if (amount < 1) {
            return "Amount should be greater than 0";
        }

        User userObj = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id:" + userId));

        System.out.println("This is use obj" + userObj);

        if (!userObj.getAccountDetails().getAccountNumber().equals(accountNumber)) {
            return "Account Number and User Id are not matched";
        }

        newTransaction.setUser(userObj);
        newTransaction.setAmount(amount);
        newTransaction.setType(Transaction.Type.DEPOSIT);

        AccountDetails accountDetails = userObj.getAccountDetails();

        accountDetails.setBalance(accountDetails.getBalance().add(BigDecimal.valueOf(amount)));
        Transaction result = transactionRepository.save(newTransaction);

        System.out.println("i am from Transaction" + result);

        return "success";
    }

    @Transactional
    public String withdrawTransaction(String accountNumber, String type, Long amount, Long userId) {
        Transaction newTransaction = new Transaction();

        if (accountNumber == null || !type.equals("Withdraw") || amount == null) {
            return "Account No or amount are required";
        }

        if (amount < 1) {
            return "Amount should be greater than 0";
        }

        User userObj = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id:" + userId));


        if (!userObj.getAccountDetails().getAccountNumber().equals(accountNumber)) {
            return "Account Number and User Id are not matched";
        }


        newTransaction.setUser(userObj);
        newTransaction.setAmount(amount);
        newTransaction.setType(Transaction.Type.WITHDRAW);

        AccountDetails accountDetails = userObj.getAccountDetails();


        accountDetails.setBalance(accountDetails.getBalance().subtract(BigDecimal.valueOf(amount)));
        Transaction result = transactionRepository.save(newTransaction);

        return "success";

    }

}
