package com.owlearn.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendResponseDto {
    private Long storyId;
    private String title;
    private String reason;
    private List<String> highlights;
    private String coverImageUrl;
}
