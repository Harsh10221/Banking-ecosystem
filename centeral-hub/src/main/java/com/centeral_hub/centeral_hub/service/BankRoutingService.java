package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.model.BankPartners;
import com.centeral_hub.centeral_hub.repository.BankPartnersRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BankRoutingService {

    @Autowired
    BankPartnersRepository bankPartnersRepository;

    public final static Map<String, String> bankUrlMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadConfigs() {

        bankUrlMap.clear();

        bankPartnersRepository.findAll().forEach(config ->
                bankUrlMap.put(config.getBankName(), config.getBankBaseUrl())

        );
    }

    public String getUrlOfBank(String bankName) {
        return bankUrlMap.get(bankName);
    }


    public void addBankUrl(String bankName, String bankBaseUrl) {
        BankPartners bankPartners = new BankPartners();
        bankPartners.setBankName(bankName);
        bankPartners.setBankBaseUrl(bankBaseUrl);

        bankPartnersRepository.save(bankPartners);

        loadConfigs();
    }

}
