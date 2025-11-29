package com.owlearn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabDto {
    private String word;        // 영단어(shiny)
    private String meaning;   // 한국어 뜻 (빛을 반사해서 반짝이는, 윤이 나는)
}
