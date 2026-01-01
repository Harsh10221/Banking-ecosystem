package com.banking.net_banking_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public User registerNewUser(User user, String accountType) {
    	// 1. HASH THE PASSWORD before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 2. Set the User's account type
        user.setAccountType(accountType.toUpperCase()); 

        // 3. Generate account details
        String accountNumber = generateAccountNumber();
        AccountDetails account = new AccountDetails();
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType.toUpperCase());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");
        account.setUser(user);

        user.setAccountDetails(account); 
        return userRepository.save(user);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}