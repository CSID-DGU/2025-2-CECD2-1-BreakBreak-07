package com.owlearn.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswerResponseDto {

    @JsonProperty("isCorrect")
    private boolean isCorrect;

    private int correctIndex;

    private String explanation;

    private int selectedIndex;
}
