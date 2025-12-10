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
    private String title;
    private Integer rating;
    private String feeling;
    private LocalDateTime createdAt;
}
