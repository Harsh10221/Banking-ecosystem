package com.banking.net_banking_system.service;

import com.banking.net_banking_system.dtos.TransactionDataWebsocket;
import com.banking.net_banking_system.dtos.WebsocketDashBoardMetrics;
import com.banking.net_banking_system.dtos.WebsocketTransaction;
import com.banking.net_banking_system.repository.TransferRepository;
import com.banking.net_banking_system.utils.WebSocketLogAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DashboardSocketHandler extends TextWebSocketHandler {

    @Autowired
    TransferRepository transferRepository;

    public DashboardSocketHandler() {
        WebSocketLogAppender.socketHandler = this;
    }

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Dashboard connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Dashboard disconnected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode payload = objectMapper.readTree(message.getPayload());
        String messageType = payload.get("type").asText();

        switch (messageType) {
            case "FETCH_RECENT_TRANSACTIONS":
                TextMessage msg = fetchRecentTransferTransactions();
                session.sendMessage(msg);
            case "FETCH_METRICS":
                TextMessage dashboardMetrics = fetchDashboardMetrics();
                session.sendMessage(dashboardMetrics);

                break;
            default:
                System.out.println("Unknown command: " + messageType);
        }
    }

    public void broadcastLog(String logMessage) throws IOException {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                System.out.println("This is log " + logMessage);
                session.sendMessage(new TextMessage(logMessage));
            }
        }
    }
//    If you fetch a TransferModel, Hibernate assumes that later in your Java code, you might write transfer.getUser().getName(). If Hibernate didn't fetch the user, your app would crash with a NullPointerException

//    { id: Math.random().toString(), time: new Date().toLocaleTimeString(), level: 'ERROR', msg: `Transfer failed: ${error.message}` }

    public TextMessage fetchRecentTransferTransactions() throws JsonProcessingException {
        List<TransactionDataWebsocket> box = transferRepository.getTransactions();
        WebsocketTransaction<List<TransactionDataWebsocket>> responsePayload = new WebsocketTransaction<>("TRANSACTIONS_RESULT", box);
        String payload = objectMapper.writeValueAsString(responsePayload);

        return new TextMessage(payload);
    }

    public TextMessage fetchDashboardMetrics() throws JsonProcessingException {

        List<WebsocketDashBoardMetrics> data = transferRepository.getDashBoardMetric();
        WebsocketTransaction<List<WebsocketDashBoardMetrics>> responsePayload = new WebsocketTransaction<>("METRICS_RESULT", data);
        String payload = objectMapper.writeValueAsString(responsePayload);

        return new TextMessage(payload);

    }


}