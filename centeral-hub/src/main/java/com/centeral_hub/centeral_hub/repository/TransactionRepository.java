package com.centeral_hub.centeral_hub.repository;

import com.centeral_hub.centeral_hub.model.TransactionModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel,Long> {

}
