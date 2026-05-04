package com.jihyeon.pawNest.dto.request.chat;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ChatMessageRequest {
    private Long roomId;    // 어떤 방인지
    private String content; // 메시지 내용

}