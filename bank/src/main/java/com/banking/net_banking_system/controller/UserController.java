package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        String email = payload.get("username");
        String password = payload.get("password");


        String result = authService.login(email, password, response);
        
        if ("success".equalsIgnoreCase(result)) {
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "redirectUrl", "/home" // This is where the user goes after login
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("success", false, "message", result));
        }
    }

}
