package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.model.Transaction;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.AccountRepository;
import com.banking.net_banking_system.repository.TransactionRepository;
import com.banking.net_banking_system.repository.UserRepository;
import com.banking.net_banking_system.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Controller
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public ResponseEntity<ResponseObject<String>> depositTransaction(String accountNumber, String type, Long amount, Long userId) {
        Transaction newTransaction = new Transaction();

        if (accountNumber == null || !type.equals("Deposit") || amount == null) {
            return ResponseObject.createResponse(400, "Account number and amount are required or type invalid.", null, HttpStatus.BAD_REQUEST);
        }

        if (amount < 1) {
            return ResponseObject.createResponse(400, "Deposit amount must be at least 1.", null, HttpStatus.BAD_REQUEST);
        }


        //// Might not needed if not needed remove this
        User userObj = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id:" + userId));

//        System.out.println("This is use obj" + userObj);

        if (!userObj.getAccountDetails().getAccountNumber().equals(accountNumber)) {
            return ResponseObject.createResponse(404, "Account and userId not match", null, HttpStatus.NOT_FOUND);
        }

        newTransaction.setUser(userObj);
        newTransaction.setAmount(amount);
        newTransaction.setType(Transaction.Type.DEPOSIT);

        AccountDetails accountDetails = userObj.getAccountDetails();

        accountDetails.setBalance(accountDetails.getBalance().add(BigDecimal.valueOf(amount)));
        Transaction result = transactionRepository.save(newTransaction);

//        System.out.println("i am from Transaction" + result);

        return ResponseObject.createResponse(200, "Deposit success", null, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<ResponseObject<String>> withdrawTransaction(String accountNumber, String type, Long amount, Long userId) {
//        System.out.println("I am from withdraw");
        Transaction newTransaction = new Transaction();

        if (accountNumber == null || !type.equals("Withdraw") || amount == null) {
//            return "Account No or amount are required";
            return ResponseObject.createResponse(404, "Account No or Amount is required", null, HttpStatus.NOT_FOUND);
        }

        if (amount < 1) {
//            return "Amount should be greater than 0";
            ResponseObject.createResponse(400, "Minimum amount should be 1 ", null, HttpStatus.BAD_REQUEST);

        }

        //// Might not need this remove this if not necessaray
        User userObj = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id:" + userId));


        if (!userObj.getAccountDetails().getAccountNumber().equals(accountNumber)) {
//            System.out.println("I am inside no match");
            ResponseObject.createResponse(400, "Account Number and User Id are not matched", null, HttpStatus.BAD_REQUEST);
//            return "Account Number and User Id are not matched";
        }


//        When the balance is low from the required amount the money still depositing in the reciver
//        Issue is becasue of the exception handling the return as string response is considering true
        if (userObj.getAccountDetails().getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
            System.out.println("I am inside low balance");
            return ResponseObject.createResponse(400, "Balance is low for transaction ", null, HttpStatus.BAD_REQUEST);
        }


        newTransaction.setUser(userObj);
        newTransaction.setAmount(amount);
        newTransaction.setType(Transaction.Type.WITHDRAW);

//        AccountDetails accountDetails = userObj.getAccountDetails();

        int resultDb = accountRepository.substractBalance(accountNumber, amount);
        System.out.println("This is result Db" + resultDb);
//        accountDetails.setBalance(accountDetails.getBalance().subtract(BigDecimal.valueOf(amount)));
        Transaction result = transactionRepository.save(newTransaction);

        return ResponseObject.createResponse(200, "Withdraw success", null, HttpStatus.OK);

    }


}
