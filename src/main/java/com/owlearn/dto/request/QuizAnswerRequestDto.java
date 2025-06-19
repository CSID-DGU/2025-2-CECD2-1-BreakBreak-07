package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswerRequestDto {
    private Long taleId;
    private int questionNumber;
    private int selectedIndex;
    private Long userId;
}
