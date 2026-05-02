package com.centeral_hub.centeral_hub.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Service
public class JwtAuthentication {

    @Value("${next_gen_bank_secret}")
    private String secretKeyOfNextBank;

    /// we cannot create this method as static because of the secretkey
    public Map<String, Object> jwtVerification(KafkaConsumerDto.TokenDetails token) {

        System.out.println("Jwt verification " +token);

//        String issuer = token.path("Issuer").asText(null);
//        String bankToken = token.path("token").asText(null);

//        if (bankToken == null || issuer == null) {
//            return Map.of("isVerified", Boolean.FALSE, "Error","Token or Issuer is null");
//
//        }


        System.out.println("issuer" + token.Issuer());
        System.out.println("banktoken" + token.token());


        SecretKey key = Keys.hmacShaKeyFor(secretKeyOfNextBank.getBytes(StandardCharsets.UTF_8));

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token.token())
                    .getPayload();

            return Map.of("isVerified", Boolean.TRUE, "bankToken", claims.getSubject());


        } catch (RuntimeException e) {
            return Map.of("isVerified", Boolean.FALSE, "Error", e.getMessage());

        }

    }

}
