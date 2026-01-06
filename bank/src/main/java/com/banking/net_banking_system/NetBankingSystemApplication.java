package com.banking.net_banking_system;

//import com.banking.net_banking_system.utils.TransferData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NetBankingSystemApplication {

	public static void main(String[] args) {

//		TransferData transferData = new TransferData();
		SpringApplication.run(NetBankingSystemApplication.class, args);


	}


}
