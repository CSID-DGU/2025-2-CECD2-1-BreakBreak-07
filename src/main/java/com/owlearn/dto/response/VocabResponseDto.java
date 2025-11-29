package com.owlearn.dto.response;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabResponseDto {
    private String word;    // 영단어
    private String meaning; // 한글 뜻
}
