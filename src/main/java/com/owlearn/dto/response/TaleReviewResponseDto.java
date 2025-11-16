package com.owlearn.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleReviewResponseDto {
    private Long reviewId;
    private Long childId;
    private Long taleId;
    private Integer rating;
    private String feeling;
    private String memorableScene;
    private String lesson;
    private String question;
    private LocalDateTime createdAt;
}
