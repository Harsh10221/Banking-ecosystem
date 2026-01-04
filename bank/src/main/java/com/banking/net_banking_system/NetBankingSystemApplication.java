package com.banking.net_banking_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NetBankingSystemApplication {

	public static void main(String[] args) {
	    SpringApplication.run(NetBankingSystemApplication.class, args);		
	}
	
}
