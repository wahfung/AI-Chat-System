package com.aichat.dto;

import com.aichat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    private Long conversationId;
    private ChatMessage.MessageRole role;
    private String content;
    private LocalDateTime createdAt;
}
