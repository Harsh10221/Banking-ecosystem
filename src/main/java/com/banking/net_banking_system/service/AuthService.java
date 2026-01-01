package com.banking.net_banking_system.service;

import ch.qos.logback.core.boolex.Matcher;
import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String email,String password){

        if (email == null || password == null) {
            return "Email or password required";
        }

        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()) {
            return "No user found with this email";

        }

            User userObj = user.get();



        if (!passwordEncoder.matches(password,userObj.getPassword())) {
                return "Incorrect password";

            }

            return "success";








    }

}
