package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleRetellRequestDto {
    private Long childId;          // 자녀 ID
    private Integer sceneIndex;    // 장면 인덱스 (0 기준)
    private String userDescription; // 아이가 쓴 설명 TEXT
}
