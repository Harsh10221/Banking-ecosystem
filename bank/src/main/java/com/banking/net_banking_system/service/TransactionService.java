package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.model.Transaction;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.AccountRepository;
import com.banking.net_banking_system.repository.TransactionRepository;
import com.banking.net_banking_system.repository.UserRepository;
import com.banking.net_banking_system.utils.ResponseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public ResponseEntity<ResponseObject<String>> depositTransaction(String accountNumber, String type, Long amount, Long userId) {
        logger.info("Processing Deposit: Account={}, Amount={}", accountNumber, amount);

        // 1. Input Validation
        if (accountNumber == null || amount == null || amount < 1) {
            return ResponseObject.createResponse(400, "Invalid input: Amount must be greater than 0 and Account Number is required.", null, HttpStatus.BAD_REQUEST);
        }

        try {
            // 2. Fetch User & Account
            User userObj = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            AccountDetails accountDetails = userObj.getAccountDetails();

            // 3. Validation: Match Account to User
            if (!accountDetails.getAccountNumber().equals(accountNumber)) {
                logger.warn("Security Alert: Account Number {} does not match User ID {}", accountNumber, userId);
                return ResponseObject.createResponse(400, "Account mismatch for the provided user.", null, HttpStatus.BAD_REQUEST);
            }

            // 4. Perform Deposit
            BigDecimal depositAmount = BigDecimal.valueOf(amount);
            accountDetails.setBalance(accountDetails.getBalance().add(depositAmount));
            
            // 5. Save Changes 
            accountRepository.save(accountDetails);

            Transaction newTransaction = new Transaction();
            newTransaction.setUser(userObj);
            newTransaction.setAmount(amount);
            newTransaction.setType(Transaction.Type.DEPOSIT);
            transactionRepository.save(newTransaction);

            logger.info("Deposit Successful. New Balance: {}", accountDetails.getBalance());
            return ResponseObject.createResponse(200, "Deposit successful", null, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error processing deposit", e);
            return ResponseObject.createResponse(500, "Internal Server Error during deposit", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<ResponseObject<String>> withdrawTransaction(String accountNumber, String type, Long amount, Long userId) {
        logger.info("Processing Withdrawal: Account={}, Amount={}", accountNumber, amount);

        // 1. Input Validation
        if (accountNumber == null || amount == null || amount < 1) {
            return ResponseObject.createResponse(400, "Invalid input: Amount must be greater than 0.", null, HttpStatus.BAD_REQUEST);
        }

        try {
            // 2. Fetch User & Account
            User userObj = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            AccountDetails accountDetails = userObj.getAccountDetails();

            // 3. Validation: Match Account to User
            if (!accountDetails.getAccountNumber().equals(accountNumber)) {
                logger.warn("Security Alert: Account Number {} does not match User ID {}", accountNumber, userId);
                return ResponseObject.createResponse(400, "Account mismatch for the provided user.", null, HttpStatus.BAD_REQUEST);
            }

            // 4. Validation: Check Sufficient Balance
            BigDecimal withdrawAmount = BigDecimal.valueOf(amount);
            if (accountDetails.getBalance().compareTo(withdrawAmount) < 0) {
                logger.warn("Insufficient funds for Account: {}", accountNumber);
                return ResponseObject.createResponse(400, "Insufficient Balance", null, HttpStatus.BAD_REQUEST);
            }

            // 5. Perform Withdrawal
            // Option A: Use Safe Subtract method from Repository (Recommended for consistency with your previous code)
            int rowsUpdated = accountRepository.substractBalance(accountNumber, amount);
            
            if (rowsUpdated == 0) {
                throw new RuntimeException("Database update failed for withdrawal.");
            }

            // 6. Log Transaction
            Transaction newTransaction = new Transaction();
            newTransaction.setUser(userObj);
            newTransaction.setAmount(amount);
            newTransaction.setType(Transaction.Type.WITHDRAW);
            transactionRepository.save(newTransaction);

            logger.info("Withdrawal Successful.");
            return ResponseObject.createResponse(200, "Withdraw successful", null, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("Error processing withdrawal", e);
            return ResponseObject.createResponse(500, "Internal Server Error during withdrawal", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}