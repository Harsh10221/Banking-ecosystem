package com.banking.net_banking_system.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${central_hub_url}")
    private String centralHubUrl;

    @Bean
    public RestClient restClient(){
        return RestClient.builder()
                .baseUrl(centralHubUrl)
                .build();

    }
}
