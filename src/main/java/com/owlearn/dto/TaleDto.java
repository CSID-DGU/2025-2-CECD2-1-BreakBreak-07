package com.owlearn.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleDto {

    private String title;
    private List<String> contents;
    private List<String> imageUrls;
    private List<QuizDto> quizzes;
    private String subject;
    private String tone;
    private String artStyle;
    private String ageGroup;
}

