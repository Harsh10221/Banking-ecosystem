package com.centeral_hub.centeral_hub.repository;

import com.centeral_hub.centeral_hub.model.BankPartners;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankPartnersRepository extends JpaRepository<BankPartners,Long> {

    Optional<BankPartners> findBankPublicKeyByBankName(String bankName);
}
