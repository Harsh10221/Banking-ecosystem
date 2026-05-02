package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.dtos.CreditRequestDto;
import com.banking.net_banking_system.dtos.DebitRequestDto;
import com.banking.net_banking_system.dtos.TransferRequestDto;
import com.banking.net_banking_system.model.UserModel;
import com.banking.net_banking_system.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/deposit")
    public ResponseEntity<?> initiateDepositTransaction(@RequestBody @Valid CreditRequestDto payload) {
        return transactionService.depositTransaction(payload);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> initiateWithdrawTransaction(@RequestBody @Valid DebitRequestDto payload) {
        return transactionService.withdrawTransaction(payload);
    }


    @PostMapping("/webhook/transfer")
    public ResponseEntity<?> webHookTransfer(@RequestBody Map<String, String> payload) {
        return null;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> initiateTransfer(@RequestBody @Valid TransferRequestDto payload, @AuthenticationPrincipal UserModel authenticatedUser) {
        System.out.println("\n Received Transfer request \n ");
        return transactionService.transferTransaction(payload);
    }

}

