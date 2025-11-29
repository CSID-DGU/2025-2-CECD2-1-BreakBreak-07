package com.owlearn.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleIdResponseDto {
    private Long taleId;
    private String reason; // 새 동화 생성 시 옵션 설명 (기성 동화에서는 null 가능)
    private List<VocabResponseDto> words; // 이 동화에 등장하는 단어 리스트
}
