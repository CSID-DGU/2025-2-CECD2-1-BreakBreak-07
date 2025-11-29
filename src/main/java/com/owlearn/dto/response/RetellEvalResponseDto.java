package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetellEvalResponseDto {
    private String feedback;  // gpt 피드백
    private Integer credit;   // 0~100
}
