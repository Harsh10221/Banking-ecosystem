package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.repository.AccountRepository;
import com.banking.net_banking_system.service.TransactionService;
import com.banking.net_banking_system.service.TransferService;
import com.banking.net_banking_system.utils.FormatDataToTransferCentralHub;
import com.banking.net_banking_system.utils.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    // Endpoint for User to start the transfer
    @PostMapping("/transfermoney")
    public FormatDataToTransferCentralHub.DataObject initiateDebitRequest(@RequestBody Map<String, String> payload) {
        String senderAccountNumber = payload.get("senderAccountNo");
        Long amount = Long.parseLong(payload.get("amount"));
        String type = payload.get("type");
        String receiverAccountNumber = payload.get("receiverAccountNumber");
        String receiverBank = payload.get("receiverBank");

        return transferService.initiateWithdrawTransfer(
                senderAccountNumber,
                BigDecimal.valueOf(amount),
                type,
                receiverAccountNumber,
                receiverBank
        );
    }

    // Callback from Central Hub: Withdraw Money
    @PostMapping("/withdraw")
    public ResponseEntity<ResponseObject<String>> initiateWithdrawTransaction(@RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String type = payload.get("type");
        Long amount = Long.parseLong(payload.get("amount"));

        Long userId = fetchUserIdByAccount(accountNumber);

        return transactionService.withdrawTransaction(accountNumber, type, amount, userId);
    }

    // Callback from Central Hub: Deposit Money
    @PostMapping("/deposit")
    public ResponseEntity<ResponseObject<String>> initiateDepositTransaction(@RequestBody Map<String, String> payload) {
        String accountNumber = payload.get("accountNumber");
        String type = payload.get("type");
        Long amount = Long.parseLong(payload.get("amount"));

        Long userId = fetchUserIdByAccount(accountNumber);

        return transactionService.depositTransaction(accountNumber, type, amount, userId);
    }

    private Long fetchUserIdByAccount(String accountNumber) {
        AccountDetails account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        return account.getUser().getId();
    }
}