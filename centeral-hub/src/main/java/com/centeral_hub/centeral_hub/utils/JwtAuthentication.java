package com.centeral_hub.centeral_hub.utils;

import com.centeral_hub.centeral_hub.dtos.KafkaConsumerDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtAuthentication {

    @Value("${next_gen_bank_secret}")
    private String secretKeyOfNextBank;

    public String  jwtVerification(KafkaConsumerDto.TokenDetails token) {

        System.out.println("Jwt verification " +token);

        System.out.println("issuer" + token.getIssuer());
        System.out.println("bank token" + token.getToken());


        SecretKey key = Keys.hmacShaKeyFor(secretKeyOfNextBank.getBytes(StandardCharsets.UTF_8));

//        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token.getToken())
                    .getPayload();


        return claims.getSubject();

    }

}
