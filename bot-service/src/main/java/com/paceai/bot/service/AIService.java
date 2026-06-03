package com.paceai.bot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    private final RestClient restClient;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.models}")
    private String modelsConfig;

    private static final String SYSTEM_PROMPT = """
            Du är PaceBot, en hjälpsam AI-assistent i ett chattrum.
            Svara kort och koncist på svenska.
            Om någon frågar om löpning eller träning, ge råd som en erfaren löpcoach.
            Annars, svara vänligt på vad de skriver.
            Håll svaren under 3 meningar.
            """;

    public AIService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .build();
    }

    public String chat(String userMessage) {
        List<String> models = Arrays.asList(modelsConfig.split(","));

        for (String model : models) {
            try {
                return callModel(model.trim(), userMessage);
            } catch (Exception e) {
                log.warn("Modell {} misslyckades: {}", model.trim(), e.getMessage());
            }
        }

        return "🤖 PaceBot är tillfälligt offline!";
    }

    @SuppressWarnings("unchecked")
    private String callModel(String model, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", userMessage));

        Map response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(Map.of("model", model, "messages", messages))
                .retrieve()
                .body(Map.class);

        List<Map> choices = (List<Map>) response.get("choices");
        Map message = (Map) choices.get(0).get("message");
        return (String) message.get("content");
    }
}