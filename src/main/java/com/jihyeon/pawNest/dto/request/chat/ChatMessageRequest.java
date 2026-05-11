package com.jihyeon.pawNest.dto.request.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatMessageRequest {
    private Long commentId;
    private Long roomId;
    private String content;
    private String senderId;
    private LocalDateTime createdAt;

}