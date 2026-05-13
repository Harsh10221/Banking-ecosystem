package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.dtos.CreditRequestDto;
import com.banking.net_banking_system.dtos.DebitRequestDto;
import com.banking.net_banking_system.dtos.TransferRequestDto;
import com.banking.net_banking_system.dtos.WebhookTransferRequest;
import com.banking.net_banking_system.model.UserModel;
import com.banking.net_banking_system.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.SecurityMarker;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
@Slf4j
public class TransactionController {

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/deposit")
    public ResponseEntity<?> initiateDepositTransaction(@RequestBody @Valid CreditRequestDto payload) {
        log.info("Received Credit request");
        return transactionService.depositTransaction(payload);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> initiateWithdrawTransaction(@RequestBody @Valid DebitRequestDto payload) {
        log.info("Received Debit request");
        return transactionService.withdrawTransaction(payload);
    }


    @PostMapping("/webhook/transfer")
    public ResponseEntity<?> webHookTransfer(@RequestBody @Valid WebhookTransferRequest payload) {
        log.info("Received request from hub in WebHook [CorrelationId: {}]", payload.correlationId());
        return transactionService.transactionWebhook(payload);
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> initiateTransfer(@RequestBody @Valid TransferRequestDto payload, @AuthenticationPrincipal UserModel authenticatedUser) {
        log.info("Received Transfer request");
        return transactionService.transferTransaction(payload);
    }

}

