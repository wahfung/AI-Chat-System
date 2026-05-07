package com.aichat.service;

import com.aichat.dto.ChatRequest;
import com.aichat.dto.ChatResponse;
import com.aichat.dto.ConversationDTO;
import com.aichat.dto.MessageDTO;
import com.aichat.entity.ChatMessage;
import com.aichat.entity.Conversation;
import com.aichat.repository.ChatMessageRepository;
import com.aichat.repository.ConversationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final OllamaService ollamaService;

    public ChatService(ConversationRepository conversationRepository,
                       ChatMessageRepository chatMessageRepository,
                       OllamaService ollamaService) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.ollamaService = ollamaService;
    }

    @Transactional
    public Flux<ChatResponse> chat(ChatRequest request) {
        Long userId = request.getUserId() != null ? request.getUserId() : 1L;
        Long conversationId = request.getConversationId();

        if (conversationId == null) {
            String title = generateTitle(request.getMessage());
            Conversation conversation = Conversation.builder()
                    .title(title)
                    .userId(userId)
                    .build();
            conversation = conversationRepository.save(conversation);
            conversationId = conversation.getId();
            logger.info("Created new conversation with ID: {}", conversationId);
        }

        final Long finalConversationId = conversationId;

        ChatMessage userMessage = ChatMessage.builder()
                .conversationId(finalConversationId)
                .role(ChatMessage.MessageRole.USER)
                .content(request.getMessage())
                .build();
        chatMessageRepository.save(userMessage);

        List<ChatMessage> historyMessages = chatMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(finalConversationId);

        List<Map<String, String>> history = historyMessages.stream()
                .filter(m -> !m.getId().equals(userMessage.getId()))
                .map(m -> Map.of(
                        "role", m.getRole() == ChatMessage.MessageRole.USER ? "user" : "assistant",
                        "content", m.getContent()
                ))
                .collect(Collectors.toList());

        StringBuilder assistantContent = new StringBuilder();

        return ollamaService.streamChat(request.getMessage(), history, finalConversationId)
                .doOnNext(response -> {
                    if (response.getContent() != null) {
                        assistantContent.append(response.getContent());
                    }
                })
                .doOnComplete(() -> {
                    if (assistantContent.length() > 0) {
                        ChatMessage assistantMessage = ChatMessage.builder()
                                .conversationId(finalConversationId)
                                .role(ChatMessage.MessageRole.ASSISTANT)
                                .content(assistantContent.toString())
                                .build();
                        chatMessageRepository.save(assistantMessage);
                        logger.info("Saved assistant message for conversation: {}", finalConversationId);
                    }
                });
    }

    public List<ConversationDTO> getConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(this::toConversationDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getMessages(Long conversationId) {
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteConversation(Long conversationId) {
        chatMessageRepository.deleteByConversationId(conversationId);
        conversationRepository.deleteById(conversationId);
        logger.info("Deleted conversation: {}", conversationId);
    }

    @Transactional
    public ConversationDTO createConversation(Long userId, String title) {
        Conversation conversation = Conversation.builder()
                .title(title)
                .userId(userId)
                .build();
        conversation = conversationRepository.save(conversation);
        return toConversationDTO(conversation);
    }

    private String generateTitle(String message) {
        if (message.length() <= 20) {
            return message;
        }
        return message.substring(0, 20) + "...";
    }

    private ConversationDTO toConversationDTO(Conversation conversation) {
        return ConversationDTO.builder()
                .id(conversation.getId())
                .title(conversation.getTitle())
                .userId(conversation.getUserId())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .build();
    }

    private MessageDTO toMessageDTO(ChatMessage message) {
        return MessageDTO.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .role(message.getRole())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
