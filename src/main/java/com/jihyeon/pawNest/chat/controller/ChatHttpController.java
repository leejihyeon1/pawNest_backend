package com.jihyeon.pawNest.chat.controller;

import com.jihyeon.pawNest.chat.service.ChatRoomService;
import com.jihyeon.pawNest.chat.service.ChatService;
import com.jihyeon.pawNest.dto.request.chat.ChatRoomRequest;
import com.jihyeon.pawNest.dto.response.chat.ChatMessageResponse;
import com.jihyeon.pawNest.dto.response.chat.ChatRoomResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "Chat + webSocket 명세",description = "/api로 시작하지 않는 주소는 stomp 호출 가이드")
public class ChatHttpController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    // 1. 채팅방 생성 (게시글에서 '채팅하기' 클릭 시)
    @PostMapping("/room")
    @Schema(description = "채팅방 생성 시, 기존 채팅방이 있으면 기존 채팅방id, 없으면 새로운 채팅방 반환")
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody ChatRoomRequest request,
                                                       @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(request,userId));
    }

    // 2. 내 채팅방 리스트 조회 (로그인한 유저의 전체 채팅 목록)
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(chatRoomService.findAllRoomsByUserId(userId));
    }

    // 3. 채팅방 지난 메세지들 목록 조회
    @GetMapping("/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal String userId
    ) {
        // 현재 로그인한 유저의 ID를 넘겨서 isMine을 판별하게 함
        return ResponseEntity.ok(chatService.getChatMessages(roomId,userId));
    }
}