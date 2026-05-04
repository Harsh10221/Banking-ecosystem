package com.centeral_hub.centeral_hub.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
@Configuration
public class RestConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    System.out.println("DEBUG - Final Request URL: " + request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }
}