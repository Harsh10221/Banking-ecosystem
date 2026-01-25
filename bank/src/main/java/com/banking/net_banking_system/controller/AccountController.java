package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.service.AccountService;
import com.banking.net_banking_system.utils.ResponseObject;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/validate")
    public <T> ResponseEntity<ResponseObject<T>> requestForValidation (@RequestBody Map<String,String> payload) {
        System.out.println("This is payload"+payload);
        String accountNumber = payload.get("accountNo");

       return accountService.validateRecipientAccount(accountNumber);

    };


}
