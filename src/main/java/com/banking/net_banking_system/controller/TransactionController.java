package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

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


}
