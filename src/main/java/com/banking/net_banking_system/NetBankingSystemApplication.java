package com.banking.net_banking_system;

import com.banking.net_banking_system.model.User;
import com.banking.net_banking_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NetBankingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetBankingSystemApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserRepository userRepository) {
		return (args) -> {
			// 1. Create a new user object
			User testUser = new User();
			testUser.setFirstName("John");
			testUser.setEmail("john@example.com");

			// 2. Save it using the repository
			userRepository.save(testUser);

			System.out.println("--- Test User Saved to Neon! ---");
		};
	}


}
