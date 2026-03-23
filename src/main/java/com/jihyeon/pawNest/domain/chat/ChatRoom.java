package com.jihyeon.pawNest.domain.chat;

import com.jihyeon.pawNest.domain.board.Board;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    // 게시글과 연관관계 (어떤 공고를 보고 채팅을 시작했는지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    // 구매자(제보확인자)와 판매자(제보자) 구분
    private String senderId;
    private String receiverId;

    @Builder
    public ChatRoom(Board board, String senderId, String receiverId) {
        this.board = board;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }
}