package com.centeral_hub.centeral_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CenteralHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(CenteralHubApplication.class, args);
	}

}


//Bank Server: Verify the User's balance in your bank_db.
//
//Bank Server: Send an API call to the Hub.
//
//Central Hub: Verify the Bank's "Master Balance" in your hub_db.
//
//Central Hub: Update the Ledger (Debit Bank A / Credit Bank B).
//
//Bank Server: Once the Hub returns "Success," finalize the transaction.
