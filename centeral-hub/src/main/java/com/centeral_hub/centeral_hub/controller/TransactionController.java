package com.centeral_hub.centeral_hub.controller;

import com.centeral_hub.centeral_hub.model.BankPartners;
import com.centeral_hub.centeral_hub.repository.BankPartnersRepository;
import com.centeral_hub.centeral_hub.service.KafkaService;
import com.centeral_hub.centeral_hub.dtos.KafkaConsumerDto;
import com.centeral_hub.centeral_hub.utils.JwtAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    @Autowired
    KafkaService kafkaService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtAuthentication jwtAuthentication;

    @Autowired
    BankPartnersRepository bankPartnersRepository;

    public record ResponseDto(
            UUID correlationId
    ) {
    }

    public record RequestDto(
            String bankName,
            String token,
            UUID userRequestKey
    ) {
    }

    @PostMapping("/testkafka")
    public ResponseEntity<?> test(@RequestBody @Valid RequestDto payload) throws InterruptedException, JsonProcessingException {
//    public ResponseEntity<Map<String, UUID>> test(@RequestBody @Valid KafkaConsumerDto payload) throws InterruptedException, JsonProcessingException {
        try {
            com.centeral_hub.centeral_hub.model.BankPartners bankPartners = bankPartnersRepository.findBankPublicKeyByBankName(payload.bankName).orElseThrow(() -> new EntityNotFoundException("No bank found "));
            String tokenData = jwtAuthentication.jwtVerification(bankPartners.getBankPublicKey(), payload.token);

            KafkaConsumerDto decodedPayload = objectMapper.readValue(tokenData, KafkaConsumerDto.class);

            UUID correlationId = UUID.randomUUID();

            decodedPayload.setSenderBank(payload.bankName);
            decodedPayload.setCorrelationId(correlationId);
            decodedPayload.setUserRequestKey(payload.userRequestKey);

            kafkaService.sendTransactionToExecuteWithdraw(decodedPayload);
            ResponseDto obj = new ResponseDto(correlationId);

            return ResponseEntity.status(202).body(obj);
        } catch (RuntimeException e) {
            log.error("Error occur ", e);
            return ResponseEntity.status(500).body(Map.of("message", e.getMessage()));
        } catch (NoSuchAlgorithmException e) {
            log.error("Error occur ", e);

            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            log.error("Error occur ", e);
            throw new RuntimeException(e);
        }
    }


}
