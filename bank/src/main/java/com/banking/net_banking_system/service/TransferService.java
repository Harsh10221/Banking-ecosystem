package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.User;
import org.apache.catalina.webresources.StandardRoot;

public class TransferService {

    public String initiateWithdrawTransfer(User user, Long amount, String type,String toAccountNumber,String toBank) {

        if (user == null || amount == 0 || !type.equals("Withdraw") ) {
            return "Some required fields are null";
        }

        //api for centeral hub
        // also need to authorize to check if it's bank only
        // Data = user,amount,type,from which bank ,
        // to which user/account no, to which bank
        // is the daily limit is reached

        // froze the money as soon as the initiatewithdrawtransfer is invoke

        //first withdraw if success then deposit in dest bank

        //response from centeral-hub then call the withdraw method

        //with the required data for it .








        return "success";
    }



}

//Request to centeral hub for transfer ->
//centeral hub / request is from valid source / can user have that much money ->
//centeral hub sends response to desti bank -> check if the user can take the money ->
//dest bank to centeral hub ready to take money ->
//sourse bank money debdit / withdraw method call ->
//dest bank ka deposit method call hogayega

//In real-world banking system the first step is manual

//Manual Registration: The Bank admin registers with the Central Hub (using a physical contract or a secure admin portal).
//
//The Secret Delivery: The Central Hub generates a Client ID and a Client Secret. These are like a "Username and Password" but for servers.
//
//Storage: The Bank stores these secrets in their server's environment variables (.env or application.properties). They never send these in a normal URL or share them.