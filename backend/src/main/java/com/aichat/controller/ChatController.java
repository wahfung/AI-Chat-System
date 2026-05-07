package com.aichat.controller;

import com.aichat.dto.*;
import com.aichat.service.ChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponse> streamChat(@Valid @RequestBody ChatRequest request) {
        logger.info("Received stream chat request for conversation: {}", request.getConversationId());
        return chatService.chat(request);
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationDTO>>> getConversations(
            @RequestParam(defaultValue = "1") Long userId) {
        List<ConversationDTO> conversations = chatService.getConversations(userId);
        return ResponseEntity.ok(ApiResponse.success(conversations));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessages(
            @PathVariable Long conversationId) {
        List<MessageDTO> messages = chatService.getMessages(conversationId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<ConversationDTO>> createConversation(
            @RequestParam(defaultValue = "1") Long userId,
            @RequestParam(defaultValue = "新对话") String title) {
        ConversationDTO conversation = chatService.createConversation(userId, title);
        return ResponseEntity.ok(ApiResponse.success(conversation, "对话创建成功"));
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable Long conversationId) {
        chatService.deleteConversation(conversationId);
        return ResponseEntity.ok(ApiResponse.success(null, "对话删除成功"));
    }
}
