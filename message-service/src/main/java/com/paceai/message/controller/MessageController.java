package com.paceai.message.controller;

import com.paceai.message.entity.Message;
import com.paceai.message.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    record SendMessageRequest(Long senderId, String content) {}

    @PostMapping
    public ResponseEntity<Message> send(@RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(request.senderId(), request.content()));
    }

    @GetMapping
    public ResponseEntity<List<Message>> getAll() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }
}