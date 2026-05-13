package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BankService {

    @Autowired
    TransactionRepository transactionRepository;

    public TransactionModel getTransactionUpdate(UUID correlationId){
       TransactionModel payload =  transactionRepository.findByCorrelationId(correlationId).orElseThrow(()-> new EntityNotFoundException("No transaction found"));
        return payload;
    }
}
