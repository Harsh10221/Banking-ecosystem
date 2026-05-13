package com.centeral_hub.centeral_hub.configuration;

import com.centeral_hub.centeral_hub.service.DashboardSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DashboardSocketHandler dashboardSocketHandler;

    public WebSocketConfig(DashboardSocketHandler dashboardSocketHandler) {
        this.dashboardSocketHandler = dashboardSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(dashboardSocketHandler, "/dashboard-stream")
                .setAllowedOrigins("*");
    }
}