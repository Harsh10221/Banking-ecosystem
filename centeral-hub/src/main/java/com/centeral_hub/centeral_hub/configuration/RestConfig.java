package com.centeral_hub.centeral_hub.configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.client.RestClient;
//
//public class ApiConfig {
//
//    @Bean
//    public RestClient restClient(){
//        return RestClient.create();
//    }
//}

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
@Configuration
public class RestConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8080")
                .requestInterceptor((request, body, execution) -> {
                    // This prints the FULL URL (Base URL + relative path)
                    System.out.println("DEBUG - Final Request URL: " + request.getURI());
                    return execution.execute(request, body);
                })
                .build();
    }
}