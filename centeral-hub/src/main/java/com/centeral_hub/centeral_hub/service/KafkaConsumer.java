package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.controller.TransactionController;
import com.centeral_hub.centeral_hub.dtos.*;
import com.centeral_hub.centeral_hub.model.BankPartners;
import com.centeral_hub.centeral_hub.model.LegderModel;
import com.centeral_hub.centeral_hub.model.SettlementLogsModel;
import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.repository.BankPartnersRepository;
import com.centeral_hub.centeral_hub.repository.LedgerRepository;
import com.centeral_hub.centeral_hub.repository.SettlementRepository;
import com.centeral_hub.centeral_hub.repository.TransactionRepository;
import com.centeral_hub.centeral_hub.utils.JwtAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.derived.AnonymousTupleBasicEntityIdentifierMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import static com.centeral_hub.centeral_hub.service.BankRoutingService.bankUrlMap;


@Slf4j
@Service
public class KafkaConsumer {


//    Check current working and then remove the comments here
//        check if the execute-withdraw msg are going in diffrent partiton or

    @Autowired
    private RestClient restClient;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    SettlementRepository settlementRepository;

    @Autowired
    BankPartnersRepository bankPartnersRepository;

    @Autowired
    LedgerRepository ledgerRepository;

    @Autowired
    KafkaService kafkaService;

    @Autowired
    JwtAuthentication jwtAuthentication;

    @Autowired
    ObjectMapper objectMapper;


    @Autowired
    private TransactionTemplate transactionTemplate;


    /// kafka moves offset to mark till where the read done, so if we working on 10 msg and first 8 msg processed and get acknowledge then kafka assums all 1-8 msgs are
    /// read so it just moves the offset to 8, so that can cause a big issue but we will not trap in that issue because we use diffrent partition
    /// one to one relationship so each thread is only talking one msg and work on it so its impossible to occur that situation


//    @KafkaListener(topics = "execute-withdraw")
//    public void executeWithdrawal(@Payload @Valid KafkaConsumerDto data, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
//
//        System.out.println("\n Processing started on thread withdraw \n ");
//
//        TransactionModel transactionModel = new TransactionModel();
//        SettlementLogsModel settlementLogsModel1 = new SettlementLogsModel();
//
//        try {
//
//            WithdrawDataDtoConsumer payload = new WithdrawDataDtoConsumer(data.getSenderAccountNumber(), "DEBIT", data.getAmount(), data.getCorrelationId());
//
//            transactionModel.setSenderBank(data.getSenderBank());
//            transactionModel.setSenderAccountNumber(data.getSenderAccountNumber());
//            transactionModel.setReceiverBank(data.getReceiverBank());
//            transactionModel.setReceiverAccountNumber(data.getReceiverAccountNumber());
//            transactionModel.setAmount(data.getAmount());
//            transactionModel.setCorrelationId(data.getCorrelationId());
//
//
//            settlementLogsModel1.setCorrelationId(data.getCorrelationId());
//            settlementLogsModel1.setBankServiceName(data.getSenderBank());
//            settlementLogsModel1.setDirection(SettlementLogsModel.Direction.OUTBOUND);
//
//           String bankUrl =  bankUrlMap.get(data.getSenderBank());
//
//            ResponseEntity<WithdrawResponseDtoConsumer<JsonNode>> responseObj = restClient.post()
//                    .uri(bankUrl+"/api/transaction/withdraw")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(payload)
//                    .retrieve()
//                    .toEntity(new ParameterizedTypeReference<WithdrawResponseDtoConsumer<JsonNode>>() {
//                    });
//
//            String rawPayload = null;
//
//            if (responseObj.getBody() != null) {
//                if (responseObj.getBody().data() != null) {
//                    rawPayload = mapper.writeValueAsString(responseObj.getBody());
//                }
//            }
//
//            settlementLogsModel1.setResponseCode(responseObj.getStatusCode().value());
//            settlementLogsModel1.setRawPayload(rawPayload);
//
//            settlementLogsModel1.setRetryCount(0);
//
//            transactionTemplate.execute(status -> {
//
//                transactionModel.setStatus(TransactionModel.Status.WITHDRAW_SUCCESS);
//
//
//                LegderModel debit = new LegderModel();
//
//                debit.setCorrelationId(data.getCorrelationId());
//                debit.setTransactionType(LegderModel.Transactiontype.DEBIT);
//                debit.setAmount(data.getAmount());
//                debit.setBank(data.getSenderBank());
//                debit.setDescription("Being transfer to " + data.getReceiverBank() + " bank");
//
//
//                transactionRepository.save(transactionModel);
//                settlementRepository.save(settlementLogsModel1);
//                ledgerRepository.save(debit);
//
//                return null;
//
//            });
//
//
//            kafkaService.sendTransactionToExecuteDeposit(data);
//            ack.acknowledge();
//
//            System.out.println("\n Processing Done on thread withdraw \n ");
//
//        } catch (JwtException e) {
//            String bankUrl =  bankUrlMap.get(data.getSenderBank());
//            restClient.post()
//                    .uri(bankUrl+"/api/transaction/webhook/transfer")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(Map.of("Jwt", "Invalid token, authorization failed"))
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, (request, response) -> {
//                    })
//                    .body(JsonNode.class);
//
//            ack.acknowledge();
//            log.error("Error occur ", e);
//
//
//        } catch (RestClientResponseException | JsonProcessingException e) {
//
//            transactionTemplate.execute(status -> {
//                transactionModel.setStatus(TransactionModel.Status.WITHDRAW_FAILED);
//
//                String errorMsg = e.getMessage();
//                int statusCode = 500;
//                if (e instanceof RestClientResponseException restEx) {
//                    errorMsg = restEx.getResponseBodyAsString();
//                    statusCode = restEx.getStatusCode().value();
//
//                }
//                transactionModel.setErrorMsg(errorMsg);
//                settlementLogsModel1.setResponseCode(statusCode);
//                settlementLogsModel1.setRawPayload(errorMsg);
//
//
//                settlementLogsModel1.setRetryCount(0);
//
//                transactionRepository.save(transactionModel);
//                settlementRepository.save(settlementLogsModel1);
//
//                return null;
//
//            });
//            ack.acknowledge();
//            log.error("Error occur ", e);
//
//
//
//        } catch (Exception e) {
//            log.error("Failed to save ledger entry: {}", e.getMessage());
//
//        }
//    }



    @KafkaListener(topics = "execute-withdraw")
    public void executeWithdrawal(@Payload TransactionController.RequestDto data, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

//        log.info("Kafka Listener triggered [execute-withdraw] on partition: {}. Correlation ID: [{}]", partition, data.getCorrelationId());
        TransactionModel transactionModel = new TransactionModel();
        SettlementLogsModel settlementLogsModel1 = new SettlementLogsModel();
        KafkaConsumerDto decodedPayload = null;
        String bankUrl = bankUrlMap.get(data.getBankName());


        try {

            log.info("Fetching public key for Bank: [{}]...", data.getBankName());
            BankPartners bankPartners = bankPartnersRepository.findBankPublicKeyByBankName(data.getBankName())
                    .orElseThrow(() -> new EntityNotFoundException("No bank found "));

            log.info("Public key retrieved. Verifying RS256 JWT signature for Tracking Key: [{}]...", data.getUserRequestKey());
            String tokenData = jwtAuthentication.jwtVerification(bankPartners.getBankPublicKey(), data.getToken());

            log.info("JWT signature verified successfully. Deserializing token payload...");
            decodedPayload = objectMapper.readValue(tokenData, KafkaConsumerDto.class);

            decodedPayload.setSenderBank(data.getBankName());
            decodedPayload.setCorrelationId(data.getCorrelationId());

            System.out.println("\n This is decodedPayload " + decodedPayload );


            log.info("Preparing DEBIT payload for Sender Bank: [{}]", decodedPayload.getSenderBank());
            WithdrawDataDtoConsumer payload = new WithdrawDataDtoConsumer(decodedPayload.getSenderAccountNumber(), "DEBIT", decodedPayload.getAmount(), decodedPayload.getCorrelationId());

            transactionModel.setSenderBank(decodedPayload.getSenderBank());
            transactionModel.setSenderAccountNumber(decodedPayload.getSenderAccountNumber());
            transactionModel.setReceiverBank(decodedPayload.getReceiverBank());
            transactionModel.setReceiverAccountNumber(decodedPayload.getReceiverAccountNumber());
            transactionModel.setAmount(decodedPayload.getAmount());
            transactionModel.setCorrelationId(decodedPayload.getCorrelationId());

            settlementLogsModel1.setCorrelationId(decodedPayload.getCorrelationId());
            settlementLogsModel1.setBankServiceName(decodedPayload.getSenderBank());
            settlementLogsModel1.setDirection(SettlementLogsModel.Direction.OUTBOUND);

            log.info("Dispatching DEBIT request to Sender Bank URL: {}/api/transaction/withdraw", bankUrl);
            ResponseEntity<WithdrawResponseDtoConsumer<JsonNode>> responseObj = restClient.post()
                    .uri(bankUrl + "/api/transaction/withdraw")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<WithdrawResponseDtoConsumer<JsonNode>>() {
                    });

            log.info("Received response from Sender Bank. HTTP Status: {}", responseObj.getStatusCode());

            String rawPayload = null;

            if (responseObj.getBody() != null) {
                if (responseObj.getBody().data() != null) {
                    rawPayload = mapper.writeValueAsString(responseObj.getBody());
                }
            }

            settlementLogsModel1.setResponseCode(responseObj.getStatusCode().value());
            settlementLogsModel1.setRawPayload(rawPayload);
            settlementLogsModel1.setRetryCount(0);

            log.info("Executing database transaction: Saving WITHDRAW_SUCCESS, Settlement, and Ledger DEBIT entries...");
            final KafkaConsumerDto finalDecodedPayload = decodedPayload;

            transactionTemplate.execute(status -> {

                transactionModel.setStatus(TransactionModel.Status.WITHDRAW_SUCCESS);

                LegderModel debit = new LegderModel();

                debit.setCorrelationId(finalDecodedPayload.getCorrelationId());
                debit.setTransactionType(LegderModel.Transactiontype.DEBIT);
                debit.setAmount(finalDecodedPayload.getAmount());
                debit.setBank(finalDecodedPayload.getSenderBank());
                debit.setDescription("Being transfer to " + finalDecodedPayload.getReceiverBank() + " bank");

                transactionRepository.save(transactionModel);
                settlementRepository.save(settlementLogsModel1);
                ledgerRepository.save(debit);

                return null;

            });

            log.info("Forwarding transaction to [execute-deposit] topic for Correlation ID: [{}]", decodedPayload.getCorrelationId());
            kafkaService.sendTransactionToExecuteDeposit(decodedPayload);

            ack.acknowledge();
            log.info("Processing Done on thread withdraw for Correlation ID: [{}]", decodedPayload.getCorrelationId());

        } catch (JwtException e) {
            log.warn("JWT Authorization failed for Correlation ID: [{}]. Notifying Sender Bank webhook...", decodedPayload.getCorrelationId());

//            BankWebhookRequest payload = new BankWebhookRequest(data.getCorrelationId(), BankWebhookRequest.TransactionStatusWebhook.REJECTED,"Json parsing failed")

//            restClient.post()
//                    .uri(bankUrl+"/api/transaction/webhook/transfer")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(payload)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, (request, response) -> {
//                    })
//                    .body(JsonNode.class);

            ack.acknowledge();

            notifyBank(bankUrl, decodedPayload, BankWebhookRequest.TransactionStatusWebhook.REJECTED, "Jwt exception");

            log.error("JWT Error occurred for Correlation ID: [{}]: {}", decodedPayload.getCorrelationId(), e.getMessage(), e);

        } catch (RestClientResponseException | JsonProcessingException e) {

            log.error("Withdrawal communication failed for Correlation ID: [{}]. Rolling back status to WITHDRAW_FAILED...", decodedPayload.getCorrelationId());

            transactionTemplate.execute(status -> {
                transactionModel.setStatus(TransactionModel.Status.WITHDRAW_FAILED);

                String errorMsg = e.getMessage();
                int statusCode = 500;
                if (e instanceof RestClientResponseException restEx) {
                    errorMsg = restEx.getResponseBodyAsString();
                    statusCode = restEx.getStatusCode().value();
                }
                transactionModel.setErrorMsg(errorMsg);
                settlementLogsModel1.setResponseCode(statusCode);
                settlementLogsModel1.setRawPayload(errorMsg);
                settlementLogsModel1.setRetryCount(0);

                transactionRepository.save(transactionModel);
                settlementRepository.save(settlementLogsModel1);

                return null;

            });
            ack.acknowledge();
            notifyBank(bankUrl, decodedPayload, BankWebhookRequest.TransactionStatusWebhook.REJECTED, "Json parsing or response error");
            log.error("Error occurred during withdraw external call for Correlation ID: [{}]: {}", decodedPayload.getCorrelationId(), e.getMessage(), e);

        }catch (NoSuchAlgorithmException e) {
            log.error("SECURITY ERROR: Missing cryptographic algorithm while verifying JWT for Tracking Key: [{}]. Error: {}", decodedPayload.getUserRequestKey(), e.getMessage(), e);
            ack.acknowledge();
            notifyBank(bankUrl, decodedPayload, BankWebhookRequest.TransactionStatusWebhook.REJECTED, "Jwt auth failed");

        } catch (InvalidKeySpecException e) {
            log.error("SECURITY ERROR: Invalid Public Key specification for Bank [{}] on Tracking Key: [{}]. Error: {}", decodedPayload.getSenderBank(), decodedPayload.getUserRequestKey(), e.getMessage(), e);
            ack.acknowledge();
            notifyBank(bankUrl, decodedPayload, BankWebhookRequest.TransactionStatusWebhook.REJECTED, "Invalid pub key");
        }
        catch (Exception e) {
            log.error("CRITICAL: Failed to process withdraw entirely for Correlation ID: [{}]. Error: {}", decodedPayload.getCorrelationId(), e.getMessage(), e);
        }
    }

//    @KafkaListener(topics = "execute-deposit")
//    public void executeDeposit(@Payload @Valid KafkaConsumerDto depositMsg, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) throws InterruptedException {
//
//        System.out.println("\n Processing started on thread Deposit \n ");
//
//        SettlementLogsModel settlementLogsModel = new SettlementLogsModel();
//
//        try {
//
//            DepositRequestBodyConsumerDto payload = new DepositRequestBodyConsumerDto(depositMsg.getSenderAccountNumber(), depositMsg.getAmount(), depositMsg.getReceiverAccountNumber(), depositMsg.getSenderBank(), depositMsg.getCorrelationId());
//
//            settlementLogsModel.setCorrelationId(depositMsg.getCorrelationId());
//            settlementLogsModel.setBankServiceName(depositMsg.getReceiverBank());
//            settlementLogsModel.setDirection(SettlementLogsModel.Direction.OUTBOUND);
//
//            String bankUrl =  bankUrlMap.get(depositMsg.getReceiverBank());
//
//            ResponseEntity<DepositResponseConsumerDto<String>> responseObj = restClient.post()
//                    .uri(bankUrl+"/api/transaction/deposit")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(payload)
//                    .retrieve()
//                    .toEntity(new ParameterizedTypeReference<DepositResponseConsumerDto<String>>() {
//                    });
//
//            System.out.println("This is response obj deposit" + responseObj);
//
//
//            String rawPayload = null;
//
//            if (responseObj.getBody() != null) {
//                rawPayload = mapper.writeValueAsString(responseObj.getBody());
//            }
//
//            System.out.println("This is raw payload" + rawPayload);
//
//            settlementLogsModel.setResponseCode(responseObj.getStatusCode().value());
//
//            settlementLogsModel.setRawPayload(rawPayload);
//            settlementLogsModel.setRetryCount(0);
//
//
//            final KafkaConsumerDto finalDepositMsg = depositMsg;
//            /// The lambda work in asynchronously some time the main method finished but the lambda still running so java enfource us, we should not change the variable data
//            ///  if we do that the variable used in lambda could be out of sync.
//            transactionTemplate.execute(status -> {
//
//                transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_SUCCESS, finalDepositMsg.getCorrelationId());
//
//                LegderModel credit = new LegderModel();
//
//                credit.setCorrelationId(finalDepositMsg.getCorrelationId());
//                credit.setTransactionType(LegderModel.Transactiontype.CREDIT);
//                credit.setAmount(finalDepositMsg.getAmount());
//                credit.setBank(finalDepositMsg.getReceiverBank());
//                credit.setDescription("Being received from " + finalDepositMsg.getSenderBank() + " bank");
//                credit.setDescription("Being received from " + "Next_BANK" + " bank");
//
//                settlementRepository.save(settlementLogsModel);
//                ledgerRepository.save(credit);
//
//                return null;
//
//            });
//
//            ack.acknowledge();
//            System.out.println("\n Processing Ended on thread Deposit \n ");
//
//        } catch (JsonProcessingException e) {
//
//            transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, depositMsg.getCorrelationId());
//            ack.acknowledge();
//
//
//        } catch (RestClientResponseException e) {
//            KafkaConsumerDto finalDepositMsg1 = depositMsg;
//
//            transactionTemplate.execute(status -> {
//                transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, finalDepositMsg1.getCorrelationId());
//
//
//                settlementLogsModel.setResponseCode(e.getStatusCode().value());
//                settlementLogsModel.setRawPayload(e.getResponseBodyAsString());
//                settlementLogsModel.setRetryCount(0);
//
//                settlementRepository.save(settlementLogsModel);
//                return null;
//
//            });
//
//            log.error("Error occur {}", e.getMessage());
//            try {
//                kafkaService.sendTransactionToExecuteCompensation(depositMsg);
//            } catch (JsonProcessingException ex) {
//                transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, depositMsg.getCorrelationId());
//                log.error("Error occur ", e);
//            }
//            ack.acknowledge();
//
//        } catch (Exception e) {
//            log.error("Error catch block", e);
//        }
//
//    }

    @KafkaListener(topics = "execute-deposit")
    public void executeDeposit(@Payload @Valid KafkaConsumerDto depositMsg, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) throws InterruptedException, JsonProcessingException {

        log.info("Kafka Listener triggered [execute-deposit] on partition: {}. Correlation ID: [{}]", partition, depositMsg.getCorrelationId());

        SettlementLogsModel settlementLogsModel = new SettlementLogsModel();
        String bankUrl = bankUrlMap.get(depositMsg.getReceiverBank());


        try {
            log.info("Preparing DEPOSIT payload for Receiver Bank: [{}]", depositMsg.getReceiverBank());
            DepositRequestBodyConsumerDto payload = new DepositRequestBodyConsumerDto(depositMsg.getSenderAccountNumber(), depositMsg.getAmount(), depositMsg.getReceiverAccountNumber(), depositMsg.getSenderBank(), depositMsg.getCorrelationId());

            settlementLogsModel.setCorrelationId(depositMsg.getCorrelationId());
            settlementLogsModel.setBankServiceName(depositMsg.getReceiverBank());
            settlementLogsModel.setDirection(SettlementLogsModel.Direction.OUTBOUND);


            log.info("Dispatching DEPOSIT request to Receiver Bank URL: {}/api/transaction/deposit", bankUrl);
            ResponseEntity<DepositResponseConsumerDto<String>> responseObj = restClient.post()
                    .uri(bankUrl + "/api/transaction/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<DepositResponseConsumerDto<String>>() {
                    });

            log.info("Received response from Receiver Bank. HTTP Status: {}", responseObj.getStatusCode());

            String rawPayload = null;

            if (responseObj.getBody() != null) {
                rawPayload = mapper.writeValueAsString(responseObj.getBody());
            }

            log.info("Raw payload parsed successfully for Correlation ID: [{}]", depositMsg.getCorrelationId());

            settlementLogsModel.setResponseCode(responseObj.getStatusCode().value());
            settlementLogsModel.setRawPayload(rawPayload);
            settlementLogsModel.setRetryCount(0);

            final KafkaConsumerDto finalDepositMsg = depositMsg;

            log.info("Executing database transaction: Updating DEPOSIT_SUCCESS, Settlement, and Ledger CREDIT entries...");
            transactionTemplate.execute(status -> {

                transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_SUCCESS, finalDepositMsg.getCorrelationId());

                LegderModel credit = new LegderModel();

                credit.setCorrelationId(finalDepositMsg.getCorrelationId());
                credit.setTransactionType(LegderModel.Transactiontype.CREDIT);
                credit.setAmount(finalDepositMsg.getAmount());
                credit.setBank(finalDepositMsg.getReceiverBank());
                credit.setDescription("Being received from " + finalDepositMsg.getSenderBank() + " bank");
                credit.setDescription("Being received from " + "Next_BANK" + " bank");

                settlementRepository.save(settlementLogsModel);
                ledgerRepository.save(credit);

                return null;

            });

            ack.acknowledge();
            log.info("Processing Done on thread Deposit for Correlation ID: [{}]", depositMsg.getCorrelationId());
            notifyBank(bankUrl, depositMsg, BankWebhookRequest.TransactionStatusWebhook.SUCCESS, null);


        } catch (JsonProcessingException e) {
            log.error("JSON Processing failed for Correlation ID: [{}]. Updating status to DEPOSIT_FAILED.", depositMsg.getCorrelationId(), e);
            transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, depositMsg.getCorrelationId());
            kafkaService.sendTransactionToExecuteCompensation(depositMsg);
            ack.acknowledge();
            notifyBank(bankUrl, depositMsg, BankWebhookRequest.TransactionStatusWebhook.REJECTED, "Json parsing error");

        } catch (RestClientResponseException e) {
            log.warn("Receiver Bank rejected DEPOSIT for Correlation ID: [{}]. HTTP Status: {}. Initiating COMPENSATION flow...", depositMsg.getCorrelationId(), e.getStatusCode());

            KafkaConsumerDto finalDepositMsg1 = depositMsg;

            transactionTemplate.execute(status -> {
                transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, finalDepositMsg1.getCorrelationId());

                settlementLogsModel.setResponseCode(e.getStatusCode().value());
                settlementLogsModel.setRawPayload(e.getResponseBodyAsString());
                settlementLogsModel.setRetryCount(0);

                settlementRepository.save(settlementLogsModel);
                return null;
            });

            log.error("Deposit external call error for Correlation ID: [{}]: {}", depositMsg.getCorrelationId(), e.getMessage());

            try {
                log.info("Forwarding transaction to [execute-compensation] topic for Correlation ID: [{}]", depositMsg.getCorrelationId());
                kafkaService.sendTransactionToExecuteCompensation(depositMsg);
            } catch (JsonProcessingException ex) {
                transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, depositMsg.getCorrelationId());
                log.error("CRITICAL: Failed to send compensation message to Kafka for Correlation ID: [{}]. Error: {}", depositMsg.getCorrelationId(), ex.getMessage(), ex);
            }
            ack.acknowledge();
            notifyBank(bankUrl, depositMsg, BankWebhookRequest.TransactionStatusWebhook.REJECTED, "Api resposne error");

        } catch (Exception e) {
            log.error("CRITICAL: Unexpected system failure during deposit processing for Correlation ID: [{}]. Error: {}", depositMsg.getCorrelationId(), e.getMessage(), e);
        }
    }

//
//
//    @KafkaListener(topics = "execute-compensation")
//    public void executeCompensation(@Payload @Valid KafkaConsumerDto msgData, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
//
//        System.out.println("\n Processing started on thread Compensation \n ");
//
//        System.out.println("Record " + msgData);
//        SettlementLogsModel settlementLogsModel = new SettlementLogsModel();
//
//
//        try {
//
//
//            CompensationRequestBody payload = new CompensationRequestBody(msgData.getSenderAccountNumber(), msgData.getSenderAccountNumber(), "COMPENSATION", msgData.getSenderBank(), msgData.getAmount(), msgData.getCorrelationId());
//
//            String bankUrl =  bankUrlMap.get(msgData.getSenderBank());
//
//            ResponseEntity<DepositResponseConsumerDto<JsonNode>> responseObj = restClient.post()
//                    .uri(bankUrl+"/api/transaction/deposit")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(payload)
//                    .retrieve()
//                    .toEntity(new ParameterizedTypeReference<DepositResponseConsumerDto<JsonNode>>() {
//                    });
//
//            String rawPayload = null;
//
//            if (responseObj.getBody() != null) {
//                if (responseObj.getBody().getData() != null) {
//                    rawPayload = mapper.writeValueAsString(responseObj.getBody());
//
//                }
//            }
//            final String rawPayloadFinal = rawPayload;
//
//            transactionTemplate.execute(status -> {
//
//                transactionRepository.updateStatus(TransactionModel.Status.REFUND_SUCCESS, msgData.getCorrelationId());
//                settlementLogsModel.setCorrelationId(msgData.getCorrelationId());
//                settlementLogsModel.setBankServiceName(msgData.getReceiverBank());
//                settlementLogsModel.setDirection(SettlementLogsModel.Direction.OUTBOUND);
//
//
//                settlementLogsModel.setResponseCode(responseObj.getStatusCode().value());
//                settlementLogsModel.setRawPayload(rawPayloadFinal);
//                settlementLogsModel.setRetryCount(0);
//
//                LegderModel credit = new LegderModel();
//
//                credit.setCorrelationId(msgData.getCorrelationId());
//                credit.setTransactionType(LegderModel.Transactiontype.CREDIT);
//                credit.setAmount(msgData.getAmount());
//                credit.setBank(msgData.getSenderBank());
//                credit.setDescription("Being refunded ");
//
//                settlementRepository.save(settlementLogsModel);
//                ledgerRepository.save(credit);
//
//                return null;
//
//            });
//
//            ack.acknowledge();
//            System.out.println("\n Processing End on thread Compensation \n ");
//
//        } catch (ResourceAccessException e) {
//            log.error("Error occur ", e);
//        } catch (JsonProcessingException | RestClientResponseException e ) {
//            transactionTemplate.execute(status -> {
//
//                transactionRepository.updateStatus(TransactionModel.Status.REFUND_FAILED, msgData.getCorrelationId());
//
//                settlementLogsModel.setCorrelationId(msgData.getCorrelationId());
//                settlementLogsModel.setBankServiceName(msgData.getReceiverBank());
//                settlementLogsModel.setDirection(SettlementLogsModel.Direction.OUTBOUND);
//                settlementLogsModel.setResponseCode(0);
//                settlementLogsModel.setRawPayload(e.getMessage());
//                settlementLogsModel.setRetryCount(0);
//                settlementRepository.save(settlementLogsModel);
//                return null;
//
//            });
//            ack.acknowledge();
//            log.error("Error occur ", e);
//
//        } catch (Exception e) {
//            log.error("Error occur ", e);
//            ack.acknowledge();
//        }
//
//    }

    @KafkaListener(topics = "execute-compensation")
    public void executeCompensation(@Payload @Valid KafkaConsumerDto msgData, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        log.info("Kafka Listener triggered [execute-compensation] on partition: {}. Correlation ID: [{}]", partition, msgData.getCorrelationId());
        log.info("Compensation Record Data: {}", msgData);

        SettlementLogsModel settlementLogsModel = new SettlementLogsModel();

        String bankUrl = bankUrlMap.get(msgData.getSenderBank());
        try {

            log.info("Preparing COMPENSATION (Refund) payload for Sender Bank: [{}]", msgData.getSenderBank());
            CompensationRequestBody payload = new CompensationRequestBody(msgData.getSenderAccountNumber(), msgData.getSenderAccountNumber(), "COMPENSATION", msgData.getSenderBank(), msgData.getAmount(), msgData.getCorrelationId());

            log.info("Dispatching COMPENSATION request to Sender Bank URL: {}/api/transaction/deposit", bankUrl);
            ResponseEntity<DepositResponseConsumerDto<JsonNode>> responseObj = restClient.post()
                    .uri(bankUrl + "/api/transaction/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<DepositResponseConsumerDto<JsonNode>>() {
                    });

            log.info("Received response from Sender Bank for COMPENSATION. HTTP Status: {}", responseObj.getStatusCode());

            String rawPayload = null;

            if (responseObj.getBody() != null) {
                if (responseObj.getBody().getData() != null) {
                    rawPayload = mapper.writeValueAsString(responseObj.getBody());
                }
            }
            final String rawPayloadFinal = rawPayload;

            log.info("Executing database transaction: Updating REFUND_SUCCESS, Settlement, and Ledger CREDIT (Refund) entries...");
            transactionTemplate.execute(status -> {

                transactionRepository.updateStatus(TransactionModel.Status.REFUND_SUCCESS, msgData.getCorrelationId());

                settlementLogsModel.setCorrelationId(msgData.getCorrelationId());
                settlementLogsModel.setBankServiceName(msgData.getReceiverBank());
                settlementLogsModel.setDirection(SettlementLogsModel.Direction.OUTBOUND);

                settlementLogsModel.setResponseCode(responseObj.getStatusCode().value());
                settlementLogsModel.setRawPayload(rawPayloadFinal);
                settlementLogsModel.setRetryCount(0);

                LegderModel credit = new LegderModel();

                credit.setCorrelationId(msgData.getCorrelationId());
                credit.setTransactionType(LegderModel.Transactiontype.CREDIT);
                credit.setAmount(msgData.getAmount());
                credit.setBank(msgData.getSenderBank());
                credit.setDescription("Being refunded ");

                settlementRepository.save(settlementLogsModel);
                ledgerRepository.save(credit);

                return null;

            });

            ack.acknowledge();
            notifyBank(bankUrl,msgData, BankWebhookRequest.TransactionStatusWebhook.REJECTED,"Transaction Reversed");
            log.info("Processing Done on thread Compensation for Correlation ID: [{}]", msgData.getCorrelationId());

        } catch (ResourceAccessException e) {
            log.error("Resource access error during compensation for Correlation ID: [{}]. Error: {}", msgData.getCorrelationId(), e.getMessage(), e);
        } catch (JsonProcessingException | RestClientResponseException e) {

            log.warn("Compensation external call failed for Correlation ID: [{}]. Rolling back status to REFUND_FAILED...", msgData.getCorrelationId());

            transactionTemplate.execute(status -> {

                transactionRepository.updateStatus(TransactionModel.Status.REFUND_FAILED, msgData.getCorrelationId());

                settlementLogsModel.setCorrelationId(msgData.getCorrelationId());
                settlementLogsModel.setBankServiceName(msgData.getReceiverBank());
                settlementLogsModel.setDirection(SettlementLogsModel.Direction.OUTBOUND);
                settlementLogsModel.setResponseCode(0);
                settlementLogsModel.setRawPayload(e.getMessage());
                settlementLogsModel.setRetryCount(0);
                settlementRepository.save(settlementLogsModel);
                return null;

            });
            ack.acknowledge();
            log.error("Error occurred during compensation external call for Correlation ID: [{}]: {}", msgData.getCorrelationId(), e.getMessage(), e);
            notifyBank(bankUrl,msgData, BankWebhookRequest.TransactionStatusWebhook.REJECTED,"Json processing or api response error");

        } catch (Exception e) {
            log.error("CRITICAL: Unexpected system failure during compensation processing for Correlation ID: [{}]. Error: {}", msgData.getCorrelationId(), e.getMessage(), e);
            ack.acknowledge();
            notifyBank(bankUrl,msgData, BankWebhookRequest.TransactionStatusWebhook.REJECTED,"Error occured ");

        }
    }


    public void notifyBank(String bankUrl, KafkaConsumerDto data, BankWebhookRequest.TransactionStatusWebhook transactionStatus, String msg) {

        BankWebhookRequest payload = new BankWebhookRequest(data.getCorrelationId(), transactionStatus, msg);

        System.out.println("THis is payload"+payload);
        restClient.post()
                .uri(bankUrl + "/api/transaction/webhook/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, response) -> {
                })
                .body(JsonNode.class);
    }

}
