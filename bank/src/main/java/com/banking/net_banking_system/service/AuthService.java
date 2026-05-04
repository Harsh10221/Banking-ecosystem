package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.UserModel;
import com.banking.net_banking_system.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.jwt.access_token}")
    private String accessTokenSecretKey;

    @Value("${app.jwt.refresh_token}")
    private String refreshTokenSecretKey;

    public String login(String email, String password, HttpServletResponse response) {

        if (email == null || password == null) {
            return "Email or password required";
        }

        Optional<UserModel> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return "No user found with this email";
        }

        UserModel userObj = user.get();

        if (!passwordEncoder.matches(password, userObj.getPassword())) {
            return "Incorrect password";
        }

        SecretKey accessTokenKey = Keys.hmacShaKeyFor(accessTokenSecretKey.getBytes(StandardCharsets.UTF_8));
        SecretKey refreshTokenKey = Keys.hmacShaKeyFor(refreshTokenSecretKey.getBytes(StandardCharsets.UTF_8));

        String accessToken = Jwts.builder()
                .subject(String.valueOf(userObj.getId()))
                .signWith(accessTokenKey)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userObj.getId()))
                .signWith(refreshTokenKey)
                .compact();

        ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(3600)
                .build();

        ResponseCookie cookie2 = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(604800)
                .build();


        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie2.toString());

        return "success";


    }

}
