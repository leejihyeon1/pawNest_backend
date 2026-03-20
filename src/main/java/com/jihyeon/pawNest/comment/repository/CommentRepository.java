package com.jihyeon.pawNest.comment.repository;

import com.jihyeon.pawNest.domain.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 모든 댓글을 가져오는 메서드
    List<Comment> findByBoardBoardIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long boardId);

    // 특정 작성자(userId)가 쓴 글 목록 조회
    // 마이페이지 본인이 쓴 글 조회용
    Page<Comment> findAllByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);

    // 댓글 한 건 조회
    Comment findByCommentIdAndDeletedAtIsNull(Long commentId);
}