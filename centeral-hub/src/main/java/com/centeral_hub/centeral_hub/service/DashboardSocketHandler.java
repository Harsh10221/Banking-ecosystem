package com.centeral_hub.centeral_hub.service;

import com.centeral_hub.centeral_hub.controller.TransactionController;
import com.centeral_hub.centeral_hub.dtos.KafkaMsgStats;
import com.centeral_hub.centeral_hub.utils.WebSocketLogAppender;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

@Component
public class DashboardSocketHandler extends TextWebSocketHandler {

    @Autowired
    TransactionController transactionController;


    private ThreadPoolTaskScheduler taskScheduler;
    private ScheduledFuture<?> cronJobTicket;

    public DashboardSocketHandler() {
        super();

        WebSocketLogAppender.socketHandler = this;

        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("WSCron-");
        taskScheduler.initialize();

    }


    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // For parsing JSON

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);

        if (cronJobTicket != null && !cronJobTicket.isCancelled()) {
            cronJobTicket.cancel(true);
        }

        cronJobTicket = taskScheduler.schedule(
                () -> {
                    try {
                        System.out.println("\n Corn job running ");
                        sendStats();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                new CronTrigger("*/30 * * * * *")
        );
        System.out.println("Dashboard connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);

        if (cronJobTicket != null && !cronJobTicket.isCancelled()) {
            cronJobTicket.cancel(true);
        }

        System.out.println("Dashboard disconnected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode payload = objectMapper.readTree(message.getPayload());
        String messageType = payload.get("type").asText();

        switch (messageType) {
            case "FETCH_RECENT_TRANSACTIONS":
                break;
            default:
                System.out.println("Unknown command: " + messageType);
        }
    }

    public void broadcastLog(String logMessage) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(logMessage));
            }
        }
    }

    public void sendStats() throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                System.out.println("I M under the sendStat");

                ResponseEntity<KafkaMsgStats> obj = transactionController.getKafkaStats();
                KafkaMsgStats payload = obj.getBody();

                String message = objectMapper.writeValueAsString(payload);
                System.out.println("This is message " + message);
                session.sendMessage(new TextMessage(message));

            }
        }
    }


}
