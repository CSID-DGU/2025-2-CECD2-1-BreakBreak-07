package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleReviewCreateRequestDto {
    private Integer rating;
    private String feeling;
    private String memorableScene;
    private String lesson;
    private String question;
}
