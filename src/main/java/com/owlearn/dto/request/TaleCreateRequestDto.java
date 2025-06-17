package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleCreateRequestDto {

    private String topic; // 동화의 주요 주제
    private String style; // 선호하는 그림체 스타일
    private int age; // 대상 연령대
}
