package com.centeral_hub.centeral_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CenteralHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(CenteralHubApplication.class, args);
	}

}

