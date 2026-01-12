package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.service.AccountService;
import com.banking.net_banking_system.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/validate")
    public String requestForValidation(@RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNo");
        return accountService.validateRecipientAccount(accountNumber);
    }

    // for Frontend Check Reciever acc number
    @PostMapping("/check")
    public ResponseEntity<ResponseObject<String>> checkReceiverAccount(@RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        try {
            String fullName = accountService.getAccountHolderName(accountNumber);
            return ResponseObject.createResponse(200, "Account Verified", fullName, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseObject.createResponse(404, e.getMessage(), null, HttpStatus.NOT_FOUND);
        }
    }
}