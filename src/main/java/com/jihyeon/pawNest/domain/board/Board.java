package com.jihyeon.pawNest.domain.board;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class) // 작성/수정일 자동화를 위해 필요
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false)
    private String writerId; // 작성자 아이디
    private String breed1;    // 품종1
    private String breed2;    // 품종2 (Google Vision 연동 예정)
    private String gender;   // 성별
    private String age;      // 나이
    private String color;      // 색깔
    private String title;      // 제목

    @Column(columnDefinition = "TEXT")
    private String features; // 특징

    private LocalDate missingDate; // 실종 날짜 (날짜만 필요하므로 LocalDate)
    private String missingLocation; // 실종 장소

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount; // 조회수

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 작성일

    @LastModifiedDate
    private LocalDateTime updatedAt; // 수정일

    private LocalDateTime deletedAt; // 삭제일 (Soft Delete용)

    //첨부파일 관련
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BoardImage> images = new ArrayList<>();

    // 수정
    public void update(String breed1, String breed2, String gender, String age, String color,String features, LocalDate missingDate, String missingLocation) {
        this.breed1 = breed1;
        this.breed2 = breed2;
        this.gender = gender;
        this.age = age;
        this.color = color;
        this.features = features;
        this.missingDate = missingDate;
        this.missingLocation = missingLocation;
    }

    // 삭제
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 조회수 증가
    public void incrementViewCount() {
        this.viewCount += 1;
    }

    // 이미지 추가 편의 메서드
    public void addImage(BoardImage image) {
        this.images.add(image);
        image.setBoard(this);
    }
    // 필드들을 조합하여 타이틀을 생성하는 메서드
    public void generateTitle() {
        // 예: [말티즈] 2살_하얀색
        this.title = String.format("[%s] %s살_%s",
                this.breed2,
                this.age,
                this.color);
    }
}