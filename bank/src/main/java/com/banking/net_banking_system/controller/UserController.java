package com.banking.net_banking_system.controller;

import com.banking.net_banking_system.service.AuthService;
//<<<<<<< HEAD
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
//=======

import jakarta.servlet.http.Cookie;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//>>>>>>> main
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpServletResponse response) {
       try{
        System.out.println(payload);
        String email = payload.get("email");
        String password = payload.get("password");
        String result = authService.login(email, password, response);
        
        if ("success".equalsIgnoreCase(result)) {
            return ResponseEntity.ok(Map.of(
                "success", true, 
                "redirectUrl", "/home" 
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Map.of("success", false, "message", result));
        }} catch (RuntimeException e) {
           throw new RuntimeException(e);
       }
    }
    
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }

}
