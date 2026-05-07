package com.aichat.controller;

import com.aichat.dto.ApiResponse;
import com.aichat.service.OllamaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    private final OllamaService ollamaService;

    public HealthController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "AI Chat Backend");
        data.put("ollamaAvailable", ollamaService.isModelAvailable());

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
