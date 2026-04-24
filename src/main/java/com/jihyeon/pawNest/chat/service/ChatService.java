package com.jihyeon.pawNest.chat.service;

import com.jihyeon.pawNest.chat.repository.ChatMessageRepository;
import com.jihyeon.pawNest.chat.repository.ChatRoomRepository;
import com.jihyeon.pawNest.domain.chat.ChatMessage;
import com.jihyeon.pawNest.domain.chat.ChatRoom;
import com.jihyeon.pawNest.dto.response.chat.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatMessages(Long roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomOrderByCreatedAtAsc(room);

        return  messages.stream()
                .map(message -> ChatMessageResponse.of(message, userId))
                .toList();
    }
}