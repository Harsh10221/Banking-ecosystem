package com.banking.net_banking_system.service;

import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Value("${app.jwt.secret}")
    private String secretKey;

    public String login(String email, String password, HttpServletResponse response) {

        if (email == null || password == null) {
            return "Email or password required";
        }

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return "No user found with this email";
        }

        User userObj = user.get();

        if (!passwordEncoder.matches(password, userObj.getPassword())) {
            return "Incorrect password";
        }

        System.out.println("This is user id " + String.valueOf(userObj.getId()));

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        String accessToken = Jwts.builder()
                .subject(String.valueOf(userObj.getId()))
                .signWith(key)
                .compact();

        Cookie cookie = new Cookie("AccessToken", accessToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);


//        System.out.println("Cookiename:" + cookie.getName());
//        System.out.println("Cookievalue:" + cookie.getValue());

        response.addCookie(cookie);

        return "success";


    }

}
