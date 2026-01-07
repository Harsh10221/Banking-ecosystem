package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.service.TransactionService;
import com.banking.net_banking_system.service.TransferService;
import com.banking.net_banking_system.utils.FormatDataToTransferCentralHub;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @Autowired
//    private FormatDataToTransferCentralHub.DataObject dataObject;

    @PostMapping("/deposit")
    public String initiateDepositTransaction(@RequestBody Map<String,String> payload ){

        String accountNumber = payload.get("accountNumber");
        String type = payload.get("type");
        Long amount = Long.parseLong(payload.get("amount"));
        Long userId = Long.parseLong(payload.get("userId"));

        System.out.println("Type from /deposit"+type);


        return transactionService.depositTransaction(accountNumber,type,amount,userId);

//        return "suces";
    }

    @PostMapping("/withdraw")
    public String initiateWithdrawTransaction(@RequestBody Map<String,String> payload ){

        String accountNumber = payload.get("accountNumber");
        String type = payload.get("type");
        Long amount = Long.parseLong(payload.get("amount"));
        Long userId = Long.parseLong(payload.get("userId"));

        System.out.println("Type from /withdrawl"+type);


        return transactionService.withdrawTransaction(accountNumber,type,amount,userId);

//        return "suces";
    }

//    need to check if we get the object or not

    @PostMapping("/transfermoney")
//    @ResponseBody
    public FormatDataToTransferCentralHub.DataObject initiateDebitRequest(@RequestBody Map<String,String> payload ){

        System.out.println("payload"+payload);

//        senderAccountNo,senderBank,amount,type,receiverAccountNumber,receiverBank,verificationToken

        String senderAccountNumber = payload.get("senderAccountNo");
        Long amount = Long.parseLong(payload.get("amount"));
        String type = payload.get("type");
        String receiverAccountNumber = payload.get("receiverAccountNumber");
        String receiverBank = payload.get("receiverBank");

//        System.out.println("Type from /transfer"+type);

        return transferService.initiateWithdrawTransfer(senderAccountNumber, BigDecimal.valueOf(amount),type,receiverAccountNumber,receiverBank);

//        return "suces";
    }


}
