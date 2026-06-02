package com.paceai.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class MessageClientService {

    private static final Logger log = LoggerFactory.getLogger(MessageClientService.class);

    private final RestClient restClient;

    public MessageClientService(@Value("${message.service.url}") String messageUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(messageUrl)
                .build();
    }

    public void sendBotMessage(String content) {
        try {
            restClient.post()
                    .uri("/messages")
                    .header("Content-Type", "application/json")
                    .body(Map.of("senderId", 999L, "content", "🤖 PaceBot: " + content))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Bot svarade: {}", content);
        } catch (Exception e) {
            log.error("Kunde inte skicka bot-svar: {}", e.getMessage());
        }
    }
}