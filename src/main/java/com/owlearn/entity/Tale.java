package com.owlearn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Tale 엔티티
 * 동화 정보를 저장하며, 제목, 내용(문단 리스트), 이미지 URL 리스트, 그리고 관련 퀴즈 리스트를 포함
 */
@Entity
@Table(name = "tales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tale {

    /**
     * 동화 기본 키 (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이 동화가 어떤 기본동화에서 파생되었는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_tale_id")
    private Tale originTale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    /**
     * 동화 제목
     */
    private String title;

    /**
     * 동화 내용 - 여러 문단으로 구성됨
     * 별도의 테이블(tale_contents)에 저장, tale_id로 참조
     */
    @ElementCollection
    @CollectionTable(name = "tale_contents", joinColumns = @JoinColumn(name = "tale_id"))
    @Column(name = "content", columnDefinition = "TEXT")
    private List<String> contents;

    /**
     * 동화 이미지 URL 목록
     * 별도의 테이블(tale_images)에 저장, tale_id로 참조
     */
    @ElementCollection
    @CollectionTable(name = "tale_images", joinColumns = @JoinColumn(name = "tale_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    /**
     * 동화에 포함된 퀴즈 리스트 (1:N 관계)
     * Cascade.ALL - 동화 저장/삭제 시 퀴즈도 같이 저장/삭제
     * orphanRemoval = true - 동화에서 퀴즈 제거 시 DB에서도 삭제
     * JoinColumn으로 quizzes 테이블에 tale_id FK 설정
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tale_id")
    private List<Quiz> quizzes;

    /**
     * 동화 평가 점수 (0~100)
     * 아직 평가되지 않은 경우 null
     */
    @Column(name = "score")
    private Integer score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaleType type;

    private LocalDateTime createdAt;

    public enum TaleType {
        PREMADE,           // 기성동화
        FROM_PREMADE,      // 기성동화 기반 생성 동화
        USER_GENERATED     // 사용자 생성 동화
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}