package com.banking.net_banking_system.service;

import com.banking.net_banking_system.utils.FormatDataToTransferCentralHub;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Service
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    @Autowired
    private AccountService accountService;

    @Value("${next_gen.jwt.secret}")
    private String secretKey;

    @Value("${central.hub.url:http://localhost:8081}") // Configurable URL with default
    private String centralHubUrl;

    private final RestClient restClient;

    public TransferService() {
        this.restClient = RestClient.create();
    }

    public FormatDataToTransferCentralHub.DataObject initiateWithdrawTransfer(String senderAccountNo, BigDecimal amount, String type, String receiverAccountNumber, String receiverBank) {

        logger.info("Initiating transfer request. Sender: {}, Receiver: {}", senderAccountNo, receiverAccountNumber);

        // 1. Input Validation
        if (senderAccountNo == null || amount.compareTo(BigDecimal.ZERO) <= 0 || !"Debit".equals(type)) {
            logger.error("Invalid transfer parameters provided.");
            throw new IllegalArgumentException("Invalid transfer parameters: Amount must be positive and type must be Debit.");
        }

        // 2. Validate Local User Status
        String validationUser = accountService.validateRecipientAccount(senderAccountNo);
        if (!"Success".equals(validationUser)) {
            logger.warn("User validation failed for account: {}", senderAccountNo);
            throw new RuntimeException("Sender account is not active or ready for transfer.");
        }

        // 3. Generate Security Token
        String verificationToken = generateToken();

        // 4. Format Payload
        String senderBank = "Next_Gen";
        var dataObject = FormatDataToTransferCentralHub.formatData(
                senderAccountNo,
                senderBank,
                amount,
                type,
                receiverAccountNumber,
                receiverBank,
                verificationToken
        );

        // 5. Send Request to Central Hub
        try {
            String response = restClient.post()
                    .uri(centralHubUrl + "/api/v1/ledger/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dataObject)
                    .retrieve()
                    .body(String.class);

            logger.info("Central Hub Response: {}", response);

            // 6. Check for Hub Rejection
            if (response != null && (response.contains("Failed") || response.contains("Error"))) {
                throw new RuntimeException("Transfer rejected by Central Hub: " + response);
            }

        } catch (Exception e) {
            logger.error("Error communicating with Central Hub", e);
            throw new RuntimeException("Transfer failed: Unable to reach Central Hub.");
        }

        return dataObject;
    }

    private String generateToken() {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject("NXT_GEN")
                .signWith(key)
                .compact();
    }
}
