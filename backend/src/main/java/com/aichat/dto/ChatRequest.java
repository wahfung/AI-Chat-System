package com.aichat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    private Long conversationId;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 10000, message = "消息内容过长")
    private String message;

    private Long userId;
}
