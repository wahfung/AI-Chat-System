package com.aichat.service;

import com.aichat.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

    private final WebClient ollamaWebClient;
    private final ObjectMapper objectMapper;

    @Value("${ollama.model:deepseek-r1:1.5b}")
    private String modelName;

    public OllamaService(WebClient ollamaWebClient) {
        this.ollamaWebClient = ollamaWebClient;
        this.objectMapper = new ObjectMapper();
    }

    public Flux<ChatResponse> streamChat(String userMessage, List<Map<String, String>> history, Long conversationId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("stream", true);

        List<Map<String, String>> messages = new java.util.ArrayList<>(history);
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        logger.info("Sending chat request to Ollama with model: {}", modelName);

        return ollamaWebClient.post()
                .uri("/api/chat")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .map(line -> parseResponse(line, conversationId))
                .onErrorResume(e -> {
                    logger.error("Error communicating with Ollama: {}", e.getMessage());
                    return Flux.just(ChatResponse.builder()
                            .conversationId(conversationId)
                            .error("AI服务暂时不可用，请稍后重试")
                            .done(true)
                            .build());
                });
    }

    private ChatResponse parseResponse(String jsonLine, Long conversationId) {
        try {
            JsonNode node = objectMapper.readTree(jsonLine);
            boolean done = node.has("done") && node.get("done").asBoolean();
            String content = "";

            if (node.has("message") && node.get("message").has("content")) {
                content = node.get("message").get("content").asText();
            }

            return ChatResponse.builder()
                    .conversationId(conversationId)
                    .content(content)
                    .done(done)
                    .build();
        } catch (Exception e) {
            logger.warn("Failed to parse Ollama response: {}", jsonLine);
            return ChatResponse.builder()
                    .conversationId(conversationId)
                    .content("")
                    .done(false)
                    .build();
        }
    }

    public boolean isModelAvailable() {
        try {
            ollamaWebClient.get()
                    .uri("/api/tags")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return true;
        } catch (Exception e) {
            logger.warn("Ollama service not available: {}", e.getMessage());
            return false;
        }
    }
}
