package com.banking.net_banking_system.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.banking.net_banking_system.model.User;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.banking.net_banking_system.model.Transaction;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.TransactionRepository;
import com.banking.net_banking_system.repository.UserRepository;
import com.banking.net_banking_system.service.AccountService;

@Controller
public class OnboardingController {

    @Autowired
    private AccountService accountService;
//<<<<<<< HEAD
//
//    @GetMapping("/")
//    public String showLandingPage() {
//        return "index";
//    }
//=======
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/")
    public String showLandingPage() {
//        System.out.println("Reached");
        return "index";
    }
    
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
    
    @GetMapping("/home")
    public String showHomePage(org.springframework.ui.Model model) {
        // Get the User ID from the SecurityContext (set by JwtAuthFilter)
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        User user = userRepository.findById(Long.parseLong(userId))
                                   .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Pass the user object to the JSP
        model.addAttribute("user", user);
        model.addAttribute("account", user.getAccountDetails());
        model.addAttribute("loans", user.getLoans());
        List<Transaction> transactions = transactionRepository.findTop5ByUserOrderByCreatedAtDesc(user);
        model.addAttribute("transactions", transactions);
        return "home";
    }
//>>>>>>> main

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