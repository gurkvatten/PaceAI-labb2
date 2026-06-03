package com.paceai.bot.consumer;

import com.paceai.bot.service.AIService;
import com.paceai.bot.service.MessageClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    private final AIService aiService;
    private final MessageClientService messageClientService;

    public MessageConsumer(AIService aiService, MessageClientService messageClientService) {
        this.aiService = aiService;
        this.messageClientService = messageClientService;
    }

    @KafkaListener(topics = "message-published", groupId = "bot-service")
    public void consume(byte[] messageBytes) {
        String message = new String(messageBytes);
        log.info("Bot konsumerade meddelande: {}", message);

        if (message.contains("PaceBot")) return;

        String content = extractContent(message);
        if (content == null || content.isBlank()) return;

        String botReply = aiService.chat(content);
        messageClientService.sendBotMessage(botReply);
    }

    private String extractContent(String json) {
        try {
            int start = json.indexOf("\"content\":\"") + 11;
            int end = json.indexOf("\"", start);
            if (start > 10 && end > start) {
                return json.substring(start, end);
            }
        } catch (Exception e) {
            log.warn("Kunde inte parsa meddelande: {}", e.getMessage());
        }
        return null;
    }
}