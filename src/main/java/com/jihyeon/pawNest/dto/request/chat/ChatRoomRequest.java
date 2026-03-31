package com.jihyeon.pawNest.dto.request.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomRequest {

    // 1. 어떤 게시글(유기동물 제보 등)을 보고 채팅을 거는지
    @Schema(description = "게시글 id",example = "1")
    private Long boardId;

    // 2. 채팅을 받는 사람 (게시글 작성자 ID)
    // 게시글 작성자
    @Schema(hidden = true)
    private String receiverId;
}