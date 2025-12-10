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
}
