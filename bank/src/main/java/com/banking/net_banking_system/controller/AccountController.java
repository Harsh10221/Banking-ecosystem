package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.service.AccountService;
import com.banking.net_banking_system.utils.ResponseObject;
<<<<<<< HEAD
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
>>>>>>> main
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
//    public ResponseEntity<ResponseDto<String>> requestForValidation (@RequestBody Map<String,String> payload) {
        System.out.println("This is payload"+payload);
        String accountNumber = payload.get("accountNo");
        return accountService.validateRecipientAccount(accountNumber);
    }

<<<<<<< HEAD
       return accountService.validateRecipientAccount(accountNumber);
//       return ResponseObj.success(403,"no msg"," No data");



    };


}

=======
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
>>>>>>> main
