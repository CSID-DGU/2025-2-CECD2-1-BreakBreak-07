package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswerResponseDto {
    private boolean isCorrect;
    private int correctIndex;
    private String explanation;
}
