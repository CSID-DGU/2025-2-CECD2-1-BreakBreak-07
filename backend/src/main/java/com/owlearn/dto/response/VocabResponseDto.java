package com.owlearn.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabResponseDto {
    private String word;        // 영단어(shiny)
    private String pos;         // 품사(adjective)
    private String meaningEn;   // 영어 풀이 (bright because it reflects light)
    private String meaningKo;   // 한국어 뜻 (빛을 반사해서 반짝이는, 윤이 나는)
    private String example;     // 예문 (Liam picked up a shiny stone...)
}
