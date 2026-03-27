package com.jihyeon.pawNest.domain.chat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom; // 어떤 방의 메시지인지

    private String senderId; // 보낸 사람 ID

    @Column(columnDefinition = "TEXT")
    private String message; // 메시지 내용

    private LocalDateTime createdAt; // 전송 시간

    @Builder
    public ChatMessage(ChatRoom chatRoom, String senderId, String message) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}