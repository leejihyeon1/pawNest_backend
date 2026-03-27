package com.jihyeon.pawNest.comment.controller;

import com.jihyeon.pawNest.comment.service.CommentService;
import com.jihyeon.pawNest.domain.comment.Comment;
import com.jihyeon.pawNest.dto.request.comment.CommentRequest;
import com.jihyeon.pawNest.dto.response.board.ApiResponse;
import com.jihyeon.pawNest.dto.response.comment.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "comment + webSocket 명세",
        description = "/api로 시작하지 않는 주소는 stomp 호출 가이드")
@RestController
@RequiredArgsConstructor
public class CommentHttpController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록 전체 조회")
    @GetMapping("/api/comments/{boardId}")
    public List<CommentResponse> commentList(@PathVariable Long boardId,
                                             @AuthenticationPrincipal String loginId){
       return commentService.commentList(boardId,loginId);
    }

    @Operation(summary = "내가 쓴 댓글 전체 조회(페이징/검색)")
    @GetMapping("/api/myComments")
    public Page<CommentResponse> myComments(
            @AuthenticationPrincipal String userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return commentService.getMyComments(userId, pageable);
    }

    @Operation(summary = "댓글 수정")
    @PatchMapping("/api/comment/{commentId}")
    public ApiResponse<Long> updateComment(@RequestBody CommentRequest commentRequest,
                                     @PathVariable Long commentId,
                                     @AuthenticationPrincipal String loginId){
        try {
            //validation check
            Comment comment = commentService.getComment(commentId);
            if(comment == null) return ApiResponse.error("존재하지 않는 댓글입니다.");
            if(!comment.getUserId().equals(loginId)) return ApiResponse.error("본인이 작성한 댓글만 수정 가능합니다.");


            // 댓글 수정
            commentService.updateComment(commentId,commentRequest);

            return ApiResponse.success("댓글 수정되었습니다.");
        }catch (Exception e){
            return ApiResponse.error("댓글 수정 중 오류가 발생했습니다 : "+e.getMessage());
        }

    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/api/comment/{commentId}")
    public ApiResponse<Long> deleteComment(@PathVariable Long commentId,
                                             @AuthenticationPrincipal String loginId){
        try {
            //validation check
            Comment comment = commentService.getComment(commentId);
            if(comment == null) return ApiResponse.error("존재하지 않는 댓글입니다.");
            if(!comment.getUserId().equals(loginId)) return ApiResponse.error("본인이 작성한 댓글만 삭제 가능합니다.");


            // 댓글 삭제
            commentService.deleteComment(commentId);

            return ApiResponse.success("댓글 삭제되었습니다.");
        }catch (Exception e){
            return ApiResponse.error("댓글 삭제 중 오류가 발생했습니다 : "+e.getMessage());
        }
    }

    @Operation(
            summary = "댓글 전송 [SEND]",
            description = "주소: **/app/comment/{boardId}** <br> " +
                    "설명: 특정 게시글에 댓글을 전송합니다. <br> " +
                    "**제약사항**: 댓글 내용은 **1자 이상 200자 이하**여야 합니다. <br> " +
                    "**주의**: 1 . WebSocket 연결 후 STOMP SEND 프레임을 사용하세요. <br>" +
                    "2 . 댓글 전송 전에 에러 채널을 구독하세요. <br>" +
                    "**connect 시 필요** : 연결 헤더 - Authorization : Bearer {token}"
    )
    @PostMapping("/app/comment/{boardId}")
    public void sendComment(
            @PathVariable Long boardId,
            @RequestBody @Valid CommentRequest request) {
        // 이 메서드는 Swagger 표시용입니다.
    }

    @Operation(
            summary = "댓글 실시간 수신 [SUBSCRIBE]",
            description = "주소: **/topic/board/{boardId}** <br> " +
                    "설명: 이 주소를 구독하면 새로운 댓글이 달릴 때마다 실시간으로 데이터를 받습니다."
    )
    @GetMapping("/topic/board/{boardId}")
    public void subscribeBoard(@PathVariable Long boardId) {
        // 이 메서드는 Swagger 표시용입니다.
    }

    @Operation(
            summary = "에러 수신 [SUBSCRIBE]",
            description = "주소: **/user/queue/errors** <br> " +
                    "설명: 글자 수 제한 위반 등 검증 실패 시 에러 메시지를 수신하는 개인 채널입니다.<br>" +
                    "**댓글 전송과 같이 에러 수신 구독 필요 즉, 2개의 채널 구독 필요**"
    )
    @GetMapping("/user/queue/errors")
    public String errorDocs() { return null; }
}