package com.jihyeon.pawNest.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jihyeon.pawNest.domain.comment.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    @Schema(description = "댓글 레코드키")
    private Long commentId;

    @Schema(description = "댓글 내용", example = "안녕하세요, 실시간 댓글입니다!")
    @Size(min = 1, max = 200, message = "댓글은 1자 이상 200자 이하로 작성해주세요.")
    private String content;   // 댓글 내용

    @Schema(description = "작성자 아이디", example = "tester123")
    private String userId;    // 작성자 아이디 (로그인 정보에서 가져올 값)

    @Schema(description = "전송일시",example = "2026-01-01 07:00:00")
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "삭제일시",example = "2026-01-01 07:00:00")
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;

    private boolean isMine;//내가 쓴 댓글 여부

    public CommentResponse(Comment comment,String loginId){
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.userId = comment.getUserId();
        this.createdAt = comment.getCreatedAt();

        this.isMine = this.userId.equals(loginId);
    }

}