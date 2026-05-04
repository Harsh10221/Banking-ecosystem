package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.dtos.*;
import com.centeral_hub.centeral_hub.model.LegderModel;
import com.centeral_hub.centeral_hub.model.SettlementLogsModel;
import com.centeral_hub.centeral_hub.model.TransactionModel;
import com.centeral_hub.centeral_hub.repository.LedgerRepository;
import com.centeral_hub.centeral_hub.repository.SettlementRepository;
import com.centeral_hub.centeral_hub.repository.TransactionRepository;
import com.centeral_hub.centeral_hub.utils.JwtAuthentication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

import java.util.Map;

import java.util.UUID;

import static com.centeral_hub.centeral_hub.service.BankRoutingService.bankUrlMap;


@Slf4j
@Service
public class KafkaConsumer {


    @Autowired
    private JwtAuthentication jwtAuthentication;

    @Autowired
    private RestClient restClient;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    SettlementRepository settlementRepository;

    @Autowired
    LedgerRepository ledgerRepository;

    @Autowired
    KafkaService kafkaService;

    @Autowired
    private TransactionTemplate transactionTemplate;


    /// kafka moves offset to mark till where the read done, so if we working on 10 msg and first 8 msg processed and get acknowledge then kafka assums all 1-8 msgs are
    /// read so it just moves the offset to 8, so that can cause a big issue but we will not trap in that issue because we use diffrent partition
    /// one to one relationship so each thread is only talking one msg and work on it so its impossible to occur that situation


    @KafkaListener(topics = "execute-withdraw")
//        public void executeWithdrawal(ConsumerRecord<String, String> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
    public void executeWithdrawal(@Payload @Valid KafkaConsumerDto data, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        System.out.println("\n Processing started on thread withdraw \n ");

        TransactionModel transactionModel = new TransactionModel();
        SettlementLogsModel settlementLogsModel1 = new SettlementLogsModel();

        try {

            WithdrawDataDtoConsumer payload = new WithdrawDataDtoConsumer(data.getSenderAccountNumber(), "DEBIT", data.getAmount(), data.getCorrelationId());

            transactionModel.setSenderBank(data.getSenderBank());
            transactionModel.setSenderAccountNumber(data.getSenderAccountNumber());
            transactionModel.setReceiverBank(data.getReceiverBank());
            transactionModel.setReceiverAccountNumber(data.getReceiverAccountNumber());
            transactionModel.setAmount(data.getAmount());
            transactionModel.setCorrelationId(data.getCorrelationId());


            settlementLogsModel1.setCorrelationId(data.getCorrelationId());
            settlementLogsModel1.setBankServiceName(data.getSenderBank());
            settlementLogsModel1.setDirection(SettlementLogsModel.Direction.OUTBOUND);

//            bankUrlMap()
           String bankUrl =  bankUrlMap.get(data.getSenderBank());

            ResponseEntity<WithdrawResponseDtoConsumer<JsonNode>> responseObj = restClient.post()
                    .uri(bankUrl+"/api/transaction/withdraw")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, ((request, response) -> {}))
                    .toEntity(new ParameterizedTypeReference<WithdrawResponseDtoConsumer<JsonNode>>() {
                    });

            String rawPayload = null;

            if (responseObj.getBody() != null) {
                if (responseObj.getBody().data() != null) {
//                    rawPayload = responseObj.getBody().data().toString();
                    rawPayload = mapper.writeValueAsString(responseObj.getBody());
                }
            }

            settlementLogsModel1.setResponseCode(responseObj.getStatusCode().value());
            settlementLogsModel1.setRawPayload(rawPayload);

            settlementLogsModel1.setRetryCount(0);

            transactionTemplate.execute(status -> {

                transactionModel.setStatus(TransactionModel.Status.WITHDRAW_SUCCESS);


                LegderModel debit = new LegderModel();

                debit.setCorrelationId(data.getCorrelationId());
                debit.setTransactionType(LegderModel.Transactiontype.DEBIT);
                debit.setAmount(data.getAmount());
                debit.setBank(data.getSenderBank());
                debit.setDescription("Being transfer to " + data.getReceiverBank() + " bank");


                transactionRepository.save(transactionModel);
                settlementRepository.save(settlementLogsModel1);
                ledgerRepository.save(debit);

                return null;

            });


            kafkaService.sendTransactionToExecuteDeposit(data);
            ack.acknowledge();

            System.out.println("\n Processing Done on thread withdraw \n ");

        } catch (JwtException e) {
            String bankUrl =  bankUrlMap.get(data.getSenderBank());
            restClient.post()
                    .uri(bankUrl+"/api/transaction/webhook/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("Jwt", "Invalid token, authorization failed"))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                    })
                    .body(JsonNode.class);

            ack.acknowledge();
            log.error("Error occur ", e);


        } catch (RestClientResponseException | JsonProcessingException e) {

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
//            System.out.println("Failed mate" + e.getMessage());
            log.error("Error occur ", e);



        } catch (Exception e) {
            log.error("Failed to save ledger entry: {}", e.getMessage());

        }
    }

    @KafkaListener(topics = "execute-deposit")
    public void executeDeposit(@Payload @Valid KafkaConsumerDto depositMsg, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) throws InterruptedException {

        System.out.println("\n Processing started on thread Deposit \n ");

        SettlementLogsModel settlementLogsModel = new SettlementLogsModel();

        try {

            DepositRequestBodyConsumerDto payload = new DepositRequestBodyConsumerDto(depositMsg.getSenderAccountNumber(), depositMsg.getAmount(), depositMsg.getReceiverAccountNumber(), depositMsg.getSenderBank(), depositMsg.getCorrelationId());

            settlementLogsModel.setCorrelationId(depositMsg.getCorrelationId());
            settlementLogsModel.setBankServiceName(depositMsg.getReceiverBank());
            settlementLogsModel.setDirection(SettlementLogsModel.Direction.OUTBOUND);

            String bankUrl =  bankUrlMap.get(depositMsg.getReceiverBank());

            ResponseEntity<DepositResponseConsumerDto<String>> responseObj = restClient.post()
                    .uri(bankUrl+"/api/transaction/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<DepositResponseConsumerDto<String>>() {
                    });

            System.out.println("This is response obj deposit" + responseObj);


            String rawPayload = null;

            if (responseObj.getBody() != null) {
                rawPayload = mapper.writeValueAsString(responseObj.getBody());
            }

            System.out.println("This is raw payload" + rawPayload);

            settlementLogsModel.setResponseCode(responseObj.getStatusCode().value());

            settlementLogsModel.setRawPayload(rawPayload);
            settlementLogsModel.setRetryCount(0);


            final KafkaConsumerDto finalDepositMsg = depositMsg;
            /// The lambda work in asynchronously some time the main method finished but the lambda still running so java enfource us, we should not change the variable data
            ///  if we do that the variable used in lambda could be out of sync.
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
            System.out.println("\n Processing Ended on thread Deposit \n ");

        } catch (JsonProcessingException e) {

            transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, depositMsg.getCorrelationId());
            ack.acknowledge();


        } catch (RestClientResponseException e) {
            KafkaConsumerDto finalDepositMsg1 = depositMsg;

            transactionTemplate.execute(status -> {
                transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, finalDepositMsg1.getCorrelationId());


                settlementLogsModel.setResponseCode(e.getStatusCode().value());
                settlementLogsModel.setRawPayload(e.getResponseBodyAsString());
                settlementLogsModel.setRetryCount(0);

                settlementRepository.save(settlementLogsModel);
                return null;

            });

            log.error("Error occur {}", e.getMessage());
            try {
                kafkaService.sendTransactionToExecuteCompensation(depositMsg);
            } catch (JsonProcessingException ex) {
                transactionRepository.updateStatus(TransactionModel.Status.DEPOSIT_FAILED, depositMsg.getCorrelationId());
                log.error("Error occur ", e);
            }
            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error catch block", e);
        }

    }

    @KafkaListener(topics = "execute-compensation")
    public void executeCompensation(@Payload @Valid KafkaConsumerDto msgData, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        System.out.println("\n Processing started on thread Compensation \n ");

        System.out.println("Record " + msgData);
        SettlementLogsModel settlementLogsModel = new SettlementLogsModel();


        try {

//            KafkaConsumerDto msgData = mapper.readValue(record.value(), KafkaConsumerDto.class);

            CompensationRequestBody payload = new CompensationRequestBody(msgData.getSenderAccountNumber(), msgData.getSenderAccountNumber(), "COMPENSATION", msgData.getSenderBank(), msgData.getAmount(), msgData.getCorrelationId());

            String bankUrl =  bankUrlMap.get(msgData.getSenderBank());

            ResponseEntity<DepositResponseConsumerDto<JsonNode>> responseObj = restClient.post()
                    .uri(bankUrl+"/api/transaction/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, ((request, response) -> {}))
                    .toEntity(new ParameterizedTypeReference<DepositResponseConsumerDto<JsonNode>>() {
                    });

            String rawPayload = null;

            if (responseObj.getBody() != null) {
                if (responseObj.getBody().getData() != null) {
                    rawPayload = mapper.writeValueAsString(responseObj.getBody());

                }
            }
            final String rawPayloadFinal = rawPayload;

            transactionTemplate.execute(status -> {

                transactionRepository.updateStatus(TransactionModel.Status.REFUND_SUCCESS, msgData.getCorrelationId());

//                SettlementLogsModel settlementLogsModel = new SettlementLogsModel();

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
            System.out.println("\n Processing End on thread Compensation \n ");

        } catch (ResourceAccessException e) {
            log.error("Error occur ", e);
        } catch (JsonProcessingException | RestClientResponseException e ) {
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
            log.error("Error occur ", e);

        } catch (Exception e) {
//            throw new RuntimeException(e);
            log.error("Error occur ", e);
            ack.acknowledge();
        }

    }

}
