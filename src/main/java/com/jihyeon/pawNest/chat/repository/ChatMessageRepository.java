package com.jihyeon.pawNest.chat.repository;

import com.jihyeon.pawNest.domain.chat.ChatMessage;
import com.jihyeon.pawNest.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방에서 가장 최근에 생성된 메시지 1개만 가져오기
    Optional<ChatMessage> findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    // 특정 방의 모든 메시지를 오래된 순서대로 조회
    List<ChatMessage> findAllByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
}