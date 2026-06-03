package com.paceai.message.service;

import com.paceai.message.entity.Message;
import com.paceai.message.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final MessageRepository messageRepository;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public MessageService(MessageRepository messageRepository, KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.messageRepository = messageRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Message sendMessage(Long senderId, String content) {
        Message message = messageRepository.save(new Message(senderId, content));

        try {
            String json = String.format(
                    "{\"messageId\":%d,\"senderId\":%d,\"content\":\"%s\"}",
                    message.getId(),
                    message.getSenderId(),
                    message.getContent()
            );
            kafkaTemplate.send("message-published", json.getBytes());
            log.info("Message published to Kafka: {}", message.getId());
        } catch (Exception e) {
            log.error("Failed to publish to Kafka: {}", e.getMessage());
        }

        return message;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
}