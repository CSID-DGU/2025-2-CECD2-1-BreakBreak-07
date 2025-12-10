package com.owlearn.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDto {

    private int questionNumber; // 퀴즈 번호 (몇 번째 퀴즈인지)
    private String question; //퀴즈 문제 문장
    private List<String> choices; // 선택지 목록 (보기 4개 고정)
    private int answerIndex; // 정답 인덱스 (choices 리스트 내 0부터 시작)
    private String explanation; // 정답 해설
}
