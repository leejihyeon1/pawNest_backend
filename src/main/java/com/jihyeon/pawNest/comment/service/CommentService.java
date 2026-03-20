package com.jihyeon.pawNest.comment.service;

import com.jihyeon.pawNest.board.repository.BoardRepository;
import com.jihyeon.pawNest.comment.repository.CommentRepository;
import com.jihyeon.pawNest.domain.board.Board;
import com.jihyeon.pawNest.domain.comment.Comment;
import com.jihyeon.pawNest.domain.user.User;
import com.jihyeon.pawNest.dto.request.comment.CommentRequest;
import com.jihyeon.pawNest.dto.response.comment.CommentResponse;
import com.jihyeon.pawNest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // 실시간 전송용 도구

    // 댓글 등록
    @Transactional
    public void saveAndSendComment(Long boardId, CommentRequest requestDto) {
        // 1. 게시글과 유저 존재 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        User user = userRepository.findByUserId(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        // 2. 댓글 엔티티 생성 및 저장
        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .board(board)
                .userId(user.getUserId())
                .build();
        commentRepository.save(comment);

        CommentResponse responseDto = CommentResponse.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .userId(comment.getUserId())
                .isMine(true) // 방금 내가 쓴 댓글이므로 일단 true로 보냅니다.
                .build();

        // 3. 실시간 전송
        // /topic/board/{boardId} 를 구독 중인 모든 클라이언트에게 DTO를 보냅니다.
        messagingTemplate.convertAndSend("/topic/board/" + boardId, responseDto);
    }

    // 댓글 목록 조회
    public List<CommentResponse> commentList(Long boardId,String loginId){
        List<Comment> list =  commentRepository.findByBoardBoardIdAndDeletedAtIsNullOrderByCreatedAtDesc(boardId);

        return list.stream().map(comment -> new CommentResponse(comment,loginId)).toList();
    }

    // 내가 쓴 글 조회
    public Page<CommentResponse> getMyComments(String userId, Pageable pageable) {
        return commentRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable)
                .map(comment -> new CommentResponse(comment,userId));
    }

    // 댓글 한 건 조회
    public Comment getComment(Long commentId){
        return commentRepository.findByCommentIdAndDeletedAtIsNull(commentId);
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId,CommentRequest commentRequest){
        Comment comment = this.getComment(commentId);
        //댓글 내용 수정
        comment.update(commentRequest.getContent());
    }

    // 삭제(soft delete)
    @Transactional
    public void deleteComment(Long commentId){
        Comment comment = this.getComment(commentId);
        comment.delete();
    }


}