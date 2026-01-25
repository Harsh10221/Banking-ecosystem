package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.utils.JwtAuthentication;
import com.centeral_hub.centeral_hub.utils.KafkaMonitorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;


@Service
public class KafkaConsumer {

    private Acknowledgment currentAck;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private JwtAuthentication jwtAuthentication;

    @Autowired
    private RestClient restClient;

    @Autowired
    KafkaMonitorService kafkaMonitorService;


//    @KafkaListener(topics = "transfer-transactions", groupId = "banking-group")
//    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
//        System.out.println("!!! MESSAGE RECEIVED !!!");
//        try {
//            String threadName = Thread.currentThread().getName();
//            System.out.println("Thread: " + threadName + " | Partition: " + partition + " | Processing: " + record.value());
//            String payload = record.value();
//
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode data = mapper.readTree(payload);
//
//            System.out.println("Formated data " + data);
//
//            String senderAccountNo = data.path("senderAccountNumber").asText(null);
//            String senderBank = data.path("senderBank").asText(null);
//            BigDecimal amount = BigDecimal.valueOf(data.path("amount").asLong(0));
//            String type = data.path("type").asText(null);
//            String receiverAccountNumber = data.path("receiverAccountNumber").asText(null);
//            String receiverBank = data.path("receiverBank").asText(null);
//            String userRequestKey = data.path("userRequestKey").asText(null);
//
//            Map<String, Object> tokenBody = jwtAuthentication.jwtVerification(data.get("token"));
//
//            if (!((boolean) tokenBody.get("isVerified"))) {
//
//                JsonNode response = restClient.post()
//                        .uri("/api/transaction/webhook/transfer")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(Map.of("Jwt", "Invalid token, authorization failed"))
//                        .retrieve()
//                        .body(JsonNode.class);
//            }
//
//            String bankToken = tokenBody.get("bankToken").toString();
//
//            System.out.println("This is token body " + tokenBody);
//
//            transactionService.processInboundTransfer(senderAccountNo, senderBank, amount, type, receiverAccountNumber, receiverBank, bankToken, userRequestKey);
//
//            double remainingMsg = kafkaMonitorService.getTrueRemaining("banking-group","transfer-transactions");
//
//
//
//
//            System.out.println("<<< " + threadName + " | Partition: " + partition + " FINISHED processing: " + record.value());
//            ack.acknowledge();
//
//            System.out.println("\n\n");
//            System.out.println("Remaining Messages to be process" + remainingMsg);
//            System.out.println("\n\n");
//
//        } catch (Exception e) {
//            System.err.println("Failed to process record: " + record.key());
//            System.out.println("Error " + e);
//            ack.acknowledge();
//
//        }
//
//    }
//
    public void commitNow() {
        if (currentAck != null) {
            currentAck.acknowledge();
            System.out.println("Manual commit successful via Spring Acknowledgment");
            currentAck = null;
        } else {
            System.out.println("Nothing to commit or already committed.");
        }
    }


//    @Autowired
//    private ConsumerFactory<String, String> consumerFactory;
//
//    private Consumer<String, String> consumer;
//
//    @PostConstruct
//    public void init() {
//        this.consumer = consumerFactory.createConsumer("banking-group", "");
//        this.consumer.subscribe(Collections.singletonList("transfer-transactions"));
//
//        //changed this line and we get the data how ?
//        this.consumer.poll(Duration.ofMillis(100));
//    }
//
//    public List<ConsumerRecord<String, String>> pullBatch() {
//        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
//
//        List<ConsumerRecord<String, String>> batch = new ArrayList<>();
//        records.forEach(batch::add);
//
//        return batch;
//    }
//
//
//
//
//    public void commitNow() {
//        consumer.commitSync();
//        System.out.println("Manual commit successfull");
//    }
}
