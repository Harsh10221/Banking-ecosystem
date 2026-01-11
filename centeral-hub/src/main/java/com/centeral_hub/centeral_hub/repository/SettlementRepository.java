package com.centeral_hub.centeral_hub.repository;

import com.centeral_hub.centeral_hub.model.SettlementLogsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<SettlementLogsModel,Long> {
}
