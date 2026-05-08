package com.jihyeon.pawNest.dto.response.chat;

import com.jihyeon.pawNest.domain.chat.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatRoomResponse {
    private Long roomId;
    private String lastMessage;//가장 마지막 채팅 메세지

    // 게시글 정보
    private Long boardId;

    // 참여자 정보
    private String senderId;
    private String receiverId;

    // 생성 시간 (방 목록 정렬 등에 활용)
    private LocalDateTime createdAt;

    // 화면에 출력 될 아이디 값
    private String displayId;

    public ChatRoomResponse(ChatRoom chatRoom,String lastMessage,String displayId) {
        this.roomId = chatRoom.getRoomId();
        this.boardId = chatRoom.getBoard().getBoardId();
        this.senderId = chatRoom.getSenderId();
        this.receiverId = chatRoom.getReceiverId();
        this.lastMessage = lastMessage;
        this.displayId = displayId;
    }
}