package com.jihyeon.pawNest.chat.controller;

import com.jihyeon.pawNest.chat.service.ChatRoomService;
import com.jihyeon.pawNest.chat.service.ChatService;
import com.jihyeon.pawNest.dto.request.chat.ChatMessageRequest;
import com.jihyeon.pawNest.dto.request.chat.ChatRoomRequest;
import com.jihyeon.pawNest.dto.response.chat.ChatMessageResponse;
import com.jihyeon.pawNest.dto.response.chat.ChatRoomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Chat + webSocket 명세",description = "/api로 시작하지 않는 주소는 stomp 호출 가이드")
public class ChatHttpController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

    // 1. 채팅방 생성 (게시글에서 '채팅하기' 클릭 시)
    @PostMapping("/api/chat/room")
    @Operation(summary = "채팅방 생성",description ="기존 채팅방이 있으면 기존 채팅방id, 없으면 새로운 채팅방 반환" )
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody ChatRoomRequest request,
                                                       @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(chatRoomService.createChatRoom(request,userId));
    }

    // 2. 내 채팅방 리스트 조회 (로그인한 유저의 전체 채팅 목록)
    @GetMapping("/api/chat/rooms")
    @Operation(summary = "내 채팅방 목록 조회")
    public ResponseEntity<List<ChatRoomResponse>> getMyRooms(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(chatRoomService.findAllRoomsByUserId(userId));
    }

    // 3. 채팅방 지난 메세지들 목록 조회
    @GetMapping("/api/chat/room/{roomId}/messages")
    @Operation(summary = "지난 메세지들 조회 (채팅방 입장 시)")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal String userId
    ) {
        // 현재 로그인한 유저의 ID를 넘겨서 isMine을 판별하게 함
        return ResponseEntity.ok(chatService.getChatMessages(roomId,userId));
    }

    @Operation(
            summary = "채팅 전송 [SEND]",
            description = "웹소켓 연결 주소: **/ws** <br> " +
                    "채팅 전송 주소: **/app/chat/message** <br> "
    )
    @GetMapping("/app/chat/message")
    public void sendComment(
            @PathVariable Long boardId,
            @RequestBody @Valid ChatMessageRequest message) {
        // 이 메서드는 Swagger 표시용입니다.
    }

    @Operation(
            summary = "채팅 실시간 수신 [SUBSCRIBE]",
            description = "주소: **/topic/chat/room/{roomId}** <br> "
    )
    @GetMapping("/topic/chat/room/{roomId}")
    public void subscribeBoard(@PathVariable Long roomId) {
        // 이 메서드는 Swagger 표시용입니다.
    }
}