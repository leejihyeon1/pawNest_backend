package com.jihyeon.pawNest.chat.controller;

import com.jihyeon.pawNest.chat.repository.ChatMessageRepository;
import com.jihyeon.pawNest.chat.repository.ChatRoomRepository;
import com.jihyeon.pawNest.domain.chat.ChatMessage;
import com.jihyeon.pawNest.domain.chat.ChatRoom;
import com.jihyeon.pawNest.dto.request.chat.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatStompController {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 채팅 전송
    @MessageMapping("/chat/message")
    public void sendMessage(@Payload ChatMessageRequest message) {
        // 1. DB에 저장하기 (방 존재 여부 확인 후)
        ChatRoom room = chatRoomRepository.findById(message.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

        ChatMessage chat = ChatMessage.builder()
                .chatRoom(room)
                .senderId(message.getUserId())
                .message(message.getMessage())
                .build();

        chatMessageRepository.save(chat);

        // 2. 이 방을 구독 중인 사람들에게 메시지 뿌리기
        // /topic/chat/room/{roomId} 주소를 구독 중인 모든 클라이언트에게 전송
        messagingTemplate.convertAndSend("/topic/chat/room/" + message.getRoomId(), message);
    }
}