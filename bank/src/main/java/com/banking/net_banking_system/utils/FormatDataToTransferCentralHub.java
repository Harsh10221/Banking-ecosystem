package com.banking.net_banking_system.utils;


import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;

@Data

public class FormatDataToTransferCentralHub {

//    private String senderAccountNo;
//    private String senderBank;
//    private BigDecimal amount;
//    private String type;
//    private String receiverAccountNo;
//    private String receiverBank;
//    private String token;
//
//    public FormatDataToTransferCentralHub(String senderAccountNo, String senderBank, BigDecimal amount, String type, String receiverAccountNo, String receiverBank, String token) {
//        this.senderAccountNo = senderAccountNo;
//        this.senderBank = senderBank;
//        this.amount = amount;
//        this.type = type;
//        this.receiverAccountNo = receiverAccountNo;
//        this.receiverBank = receiverBank;
//        this.token = token;
//    }
    @Data
    public static class DataObject {
        private String senderAccountNo;
        private String senderBank;
        private BigDecimal amount;
        private String type;
        private String receiverAccountNo;
        private String receiverBank;
        private String token;

        public DataObject(String senderAccountNo, String senderBank, BigDecimal amount, String type, String receiverAccountNo, String receiverBank, String token) {
            this.senderAccountNo = senderAccountNo;
            this.senderBank = senderBank;
            this.amount = amount;
            this.type = type;
            this.receiverAccountNo = receiverAccountNo;
            this.receiverBank = receiverBank;
            this.token = token;
        }

    }


    public static DataObject formatData(String senderAccountNo, String senderBank, BigDecimal amount, String type, String receiverAccountNo, String receiverBank, String token) {

        if (senderAccountNo == null || senderBank == null || amount.compareTo(BigDecimal.ZERO) <= 0 || !type.equals("Debit") || receiverAccountNo == null || receiverBank == null || token == null) {
//            System.out.println("I am here mate");
            System.out.println("token"+token);
            System.out.println("senderAcco"+senderAccountNo);
            System.out.println("senderbank"+senderBank);
            System.out.println("amount"+amount.compareTo(BigDecimal.ZERO));
            System.out.println("type"+!type.equals("Debit"));
            System.out.println("token"+token);
            System.out.println("receiverAccountNo"+receiverAccountNo);
            System.out.println("receiverBank"+receiverBank);
            return null;
        }
        
        return new DataObject(senderAccountNo, senderBank, amount, type, receiverAccountNo, receiverBank, token
        );

    }
}
