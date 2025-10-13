package com.owlearn.dto.request;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendRequestDto {
    private Long userId;
    private String query;
    private Integer age;  // 예: 7세
    private List<String> themes;

    @Builder.Default
    private Integer limit = 5; // 기본값
}
