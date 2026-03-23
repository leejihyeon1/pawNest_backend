package com.jihyeon.pawNest.chat.controller;

import com.jihyeon.pawNest.chat.service.ChatRoomService;
import com.jihyeon.pawNest.dto.request.chat.ChatRoomRequest;
import com.jihyeon.pawNest.dto.response.chat.ChatRoomResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatHttpController {

    private final ChatRoomService chatRoomService;

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
}