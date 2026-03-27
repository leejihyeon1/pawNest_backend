package com.jihyeon.pawNest.dto.response.chat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jihyeon.pawNest.domain.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long messageId;
    private String senderId;
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String message;
    private String createdAt;
    private boolean isMine;   // 내가 보낸 메시지인지 여부

    // Entity -> DTO 변환 메서드
    public static ChatMessageResponse of(ChatMessage chatMessage, String userId) {
        return ChatMessageResponse.builder()
                .messageId(chatMessage.getMessageId())
                .senderId(chatMessage.getSenderId())
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreatedAt().toString()) // 프론트에서 가공하도록 표준값 전송
                .isMine(chatMessage.getSenderId().equals(userId))
                .build();
    }
}