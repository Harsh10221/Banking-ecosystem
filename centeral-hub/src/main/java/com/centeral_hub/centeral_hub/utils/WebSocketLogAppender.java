package com.centeral_hub.centeral_hub.utils;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.centeral_hub.centeral_hub.service.DashboardSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class WebSocketLogAppender extends AppenderBase<ILoggingEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
    public static DashboardSocketHandler socketHandler;

    public WebSocketLogAppender() {
    }

    @Override
    public void start() {
        addInfo("Attempting to open WebSocket connection...");

        super.start();
    }

    @Override
//    When we use log.info(), the text stays strictly inside Java's memory. Logback packages that text into a neat little Java object called an ILoggingEvent
    protected void append(ILoggingEvent event) {
        if (socketHandler != null) {
            try {
                System.out.println("I am from append");
                ObjectNode logPayload = objectMapper.createObjectNode();
                String time = timeFormatter.format(Instant.ofEpochMilli(event.getTimeStamp()));
                logPayload.put("time", time);

                logPayload.put("level", event.getLevel().toString());
                logPayload.put("msg", event.getFormattedMessage());


                ObjectNode socketMessage = objectMapper.createObjectNode();
                socketMessage.put("type", "NEW_LOG_EVENT");
                socketMessage.set("payload", logPayload);

                String JsonOutput = objectMapper.writeValueAsString(socketMessage);

                System.out.println("Json output " + JsonOutput);
                socketHandler.broadcastLog(JsonOutput);
            } catch (Exception e) {
                System.err.println("ERROR IN APPENDER: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}