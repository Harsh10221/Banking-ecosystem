package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.model.AccountDetails;
import com.banking.net_banking_system.repository.AccountRepository;
import com.banking.net_banking_system.model.Transaction;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.TransactionRepository;
import com.banking.net_banking_system.repository.UserRepository;
import com.banking.net_banking_system.service.TransactionService;
import com.banking.net_banking_system.service.TransferService;
import com.banking.net_banking_system.utils.FormatDataToTransferCentralHub;
import com.banking.net_banking_system.utils.ResponseObject;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/deposit")
    public ResponseEntity<?> initiateDepositTransaction(@RequestBody Map<String, String> payload) {

    // Endpoint for User to start the transfer
    @PostMapping("/transfermoney")
    public FormatDataToTransferCentralHub.DataObject initiateDebitRequest(@RequestBody Map<String, String> payload) {
        String senderAccountNumber = payload.get("senderAccountNo");
        Long amount = Long.parseLong(payload.get("amount"));
        String type = payload.get("type");
        String receiverAccountNumber = payload.get("receiverAccountNumber");
        String receiverBank = payload.get("receiverBank");
//        Long userId = Long.parseLong(payload.get("userId"));
        /// remove the user if when not testing
        Long userId = 2L;

        System.out.println("Type from /deposit" + type);


        return transactionService.depositTransaction(accountNumber, type, amount, userId);

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
    public ResponseEntity<?> initiateWithdrawTransaction(@RequestBody Map<String, String> payload) {

        String accountNumber = payload.get("accountNumber");
        String type = payload.get("type");
        Long amount = Long.parseLong(payload.get("amount"));
//        Long userId = Long.parseLong(payload.get("userId"));
        /// remove the user if when not testing
        Long userId = 1L;
////this can be needed the userId take care of it
        System.out.println("Type from /withdrawl" + type);


        return transactionService.withdrawTransaction(accountNumber, type, amount, userId);
//        return ResponseEntity

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

//    @PostMapping("/transfermoney")
//    public FormatDataToTransferCentralHub.DataObject initiateDebitRequest(@RequestBody Map<String, String> payload) {
//
//        System.out.println("payload" + payload);
//
////        senderAccountNo,senderBank,amount,type,receiverAccountNumber,receiverBank,verificationToken
//
//        String senderAccountNumber = payload.get("senderAccountNo");
//        Long amount = Long.parseLong(payload.get("amount"));
//        String type = payload.get("type");
//        String receiverAccountNumber = payload.get("receiverAccountNumber");
//        String receiverBank = payload.get("receiverBank");
//        Long userId = Long.parseLong(payload.get("userId"));
//
////        System.out.println("Type from /transfer"+type);
//
//        return transferService.initiateWithdrawTransfer(senderAccountNumber, BigDecimal.valueOf(amount), type, receiverAccountNumber, receiverBank,userId);
//


    @PostMapping("/webhook/transfer")
    public ResponseEntity<?> webHookTransfer(@RequestBody Map<String, String> payload) {
        System.out.println("payload " + payload);


        String transactionStatus = payload.get("Status");

        Long destinationAccountNumber = Long.parseLong(payload.get("receiverAccountNumber"));
        Long senderAccountNumber = Long.parseLong(payload.get("senderAccountNumber"));
        String destinationBank = payload.get("receiverBank");
        String errorMsg = payload.get("Error");
        BigDecimal amount = BigDecimal.valueOf(Long.parseLong(payload.get("amount")));

        System.out.println("I am from webhook controller " + transactionStatus);

        return transactionService.transactionWebhook(senderAccountNumber, amount, destinationAccountNumber, destinationBank, transactionStatus, errorMsg);

    }

    @PostMapping("/transfer")
    public ResponseEntity<?> initiateTransfer(@RequestBody Map<String, String> payload) {

//       When the transactino is success or faild his status didnt changed

        //
        String sender = payload.get("senderAccountNumber");
        BigDecimal amt = new BigDecimal(payload.get("amount"));
        String t = payload.get("type");
        Long receiverAcc = Long.valueOf(payload.get("receiverAccountNumber"));
        String bank = payload.get("receiverBank");
        Long userId = Long.parseLong(payload.getOrDefault("userId","0"));

        int mst = 0;
        System.out.println("\n");
        System.out.println("Total msg " + mst++ );
        System.out.println("\n");

        return transactionService.transferTransaction(sender, amt, t, receiverAcc, bank, userId);
    }



}

//            The execution never reaches here, find the cause and fix it,
//                    Failed to process record: null
//                    also this error dont know why
