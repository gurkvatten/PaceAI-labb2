package com.paceai.bff.controller;

import com.paceai.bff.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class BffController {

    private final JwtService jwtService;
    private final RestClient authClient;
    private final RestClient messageClient;

    public BffController(
            JwtService jwtService,
            @Value("${auth.service.url}") String authUrl,
            @Value("${message.service.url}") String messageUrl
    ) {
        this.jwtService = jwtService;
        this.authClient = RestClient.builder().baseUrl(authUrl).build();
        this.messageClient = RestClient.builder().baseUrl(messageUrl).build();
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Object> register(@RequestBody Map<String, String> body) {
        return authClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> body) {
        return authClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @PostMapping("/messages")
    public ResponseEntity<Object> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> body
    ) {
        if (!isValidToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        Long userId = jwtService.getUserIdFromToken(token);
        body = new java.util.HashMap<>(body);
        body.put("senderId", userId);

        return messageClient.post()
                .uri("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(Object.class);
    }

    @GetMapping("/messages")
    public ResponseEntity<Object> getMessages(
            @RequestHeader("Authorization") String authHeader
    ) {
        if (!isValidToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return messageClient.get()
                .uri("/messages")
                .retrieve()
                .toEntity(Object.class);
    }

    private boolean isValidToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        return jwtService.validateToken(authHeader.substring(7));
    }
}