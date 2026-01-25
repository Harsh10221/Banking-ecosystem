package com.banking.net_banking_system;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NetBankingSystemApplication {

	//When a deposit or withdraw request initiated by the cetneral hub itself,
	//what should be in source ? {centeral hub send request to withdraw money or deposit so what to fill in the source field?}

	// when the centeral hub send request for the deposit, find the transaction for the deposit or withdraw is going to perform,
	// and then set that transaction id as correlationid in all of them as same

	// A method to get info about the pending transaction, {when success or fail centeral hub sends data, if any case
	// if the bank server is not listening for that particular request then bank explicitly ask about that transaction status


	public static void main(String[] args) {
		SpringApplication.run(NetBankingSystemApplication.class, args);


	}


}
