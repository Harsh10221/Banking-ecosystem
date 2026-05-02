package com.banking.net_banking_system.service;

import com.banking.net_banking_system.repository.AccountRepository;
import com.banking.net_banking_system.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banking.net_banking_system.model.AccountDetailsModel;
import com.banking.net_banking_system.model.UserModel;
import com.banking.net_banking_system.repository.UserRepository;

import java.util.Random;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
	private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public AccountService(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserModel registerNewUser(UserModel user, String accountType) {
    	
    	if (userRepository.existsByEmail(user.getEmail())) throw new RuntimeException("EXISTS_EMAIL");
        if (userRepository.existsByPhone(user.getPhone())) throw new RuntimeException("EXISTS_PHONE");
        if (userRepository.existsByPan(user.getPan())) throw new RuntimeException("EXISTS_PAN");
        if (userRepository.existsByAadhar(user.getAadhar())) throw new RuntimeException("EXISTS_AADHAR");
        
    	if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("Password field is missing in request body.");
        }
    	
    	// 1. HASH THE PASSWORD before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 2. Set the User's account type
        user.setAccountType(accountType.toUpperCase()); 

        // 3. Generate account details
        String accountNumber = generateAccountNumber();
        AccountDetailsModel account = new AccountDetailsModel();
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType.toUpperCase());
        account.setUser(user);

        user.setAccountDetailsModel(account);
        return userRepository.save(user);
    }


    public <T> ResponseEntity<ResponseObject<T>> validateRecipientAccount(String accountNumber) {

        System.out.println("Accountno"+ accountNumber);
        AccountDetailsModel account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found."));

        if (account.getStatus() != AccountDetailsModel.AccountStatus.ACTIVE) {
            return ResponseObject.createResponse(400,"Failed",null,HttpStatus.BAD_REQUEST);
        }


       return ResponseObject.createResponse(200,"Success",null, HttpStatus.ACCEPTED);

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