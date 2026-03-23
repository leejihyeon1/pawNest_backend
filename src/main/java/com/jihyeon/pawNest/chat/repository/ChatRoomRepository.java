package com.jihyeon.pawNest.chat.repository;

import com.jihyeon.pawNest.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

    // 내가 보낸 사람이거나 받은 사람인 모든 채팅방 목록 조회 (최신순)
    List<ChatRoom> findAllBySenderIdOrReceiverIdOrderByRoomIdDesc(String senderId, String receiverId);

    // 특정 게시글에서 두 사람 사이의 방이 이미 존재하는지 확인 (중복 생성 방지용)
    Optional<ChatRoom> findByBoardBoardIdAndSenderIdAndReceiverId(Long boardId, String senderId, String receiverId);

}