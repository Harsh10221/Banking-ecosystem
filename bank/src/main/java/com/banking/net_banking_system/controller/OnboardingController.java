package com.banking.net_banking_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.service.AccountService;

@Controller
public class OnboardingController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/")
    public String showLandingPage() {
        return "index";
    }

    @PostMapping("/api/onboarding/register")
    @ResponseBody 
    public String registerUser(@RequestBody User user, @RequestParam String type) {
        try {
            accountService.registerNewUser(user, type);
            return "SUCCESS";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }
}