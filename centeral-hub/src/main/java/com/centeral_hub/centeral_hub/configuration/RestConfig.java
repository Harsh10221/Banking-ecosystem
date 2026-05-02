package com.centeral_hub.centeral_hub.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
@Configuration
public class RestConfig {

    @Value("${external.banking.base.url}")
    private String baseUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
//                .baseUrl("http://localhost:8080")
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    // This prints the FULL URL (Base URL + relative path)
                    System.out.println("DEBUG - Final Request URL: " + request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }
}