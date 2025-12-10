package com.owlearn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * 퀴즈 엔티티
 * 각 퀴즈는 특정 동화(Tale)에 속하며, 문제 번호, 문제 내용, 4개의 선택지, 정답 인덱스, 해설을 가짐
 */
@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    /**
     * 퀴즈 기본 키 (자동 증가)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 퀴즈가 속한 동화 정보 (ManyToOne 관계)
     * LAZY 로딩 적용으로 필요 시에만 동화 데이터를 불러옴
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tale_id")
    private Tale tale;

    /**
     * 동화 내에서의 퀴즈 순서 번호 (몇 번째 퀴즈인지)
     */
    private int questionNumber;

    /**
     * 퀴즈 문제 내용
     */
    private String question;

    /**
     * 4개의 선택지 목록
     * @ElementCollection과 별도 테이블(quiz_choices)로 관리
     */
    @ElementCollection
    @CollectionTable(name = "quiz_choices", joinColumns = @JoinColumn(name = "quiz_id"))
    @Column(name = "choice")
    private List<String> choices;

    /**
     * 정답 선택지 인덱스 (0부터 시작, 0~3)
     */
    private int answerIndex;

    /**
     * 문제에 대한 해설
     */
    private String explanation;
}