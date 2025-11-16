package com.owlearn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tale_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 아이가 쓴 독후감인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    // 어떤 동화에 대한 독후감인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tale_id", nullable = false)
    private Tale tale;

    private Integer rating;  // 별점 (1~5)

    @Column(columnDefinition = "TEXT")
    private String feeling;          // 읽고 난 기분

    @Column(columnDefinition = "TEXT")
    private String memorableScene;   // 기억에 남는 장면

    @Column(columnDefinition = "TEXT")
    private String lesson;           // 배운 점

    @Column(columnDefinition = "TEXT")
    private String question;         // 질문

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
