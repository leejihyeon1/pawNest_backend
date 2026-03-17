package com.jihyeon.pawNest.dto.request.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @Schema(description = "댓글 내용", example = "안녕하세요, 실시간 댓글입니다!")
    @Size(min = 1, max = 200, message = "댓글은 1자 이상 200자 이하로 작성해주세요.")
    private String content;   // 댓글 내용

    @Schema(description = "작성자 아이디", example = "test")
    private String userId;    // 작성자 아이디 (로그인 정보에서 가져올 값)
}