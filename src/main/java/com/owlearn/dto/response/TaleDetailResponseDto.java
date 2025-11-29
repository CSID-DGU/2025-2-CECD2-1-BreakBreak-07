package com.owlearn.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleDetailResponseDto {

    private String title; // 동화 제목
    private List<String> contents; // 동화 내용(문장 혹은 페이지별 텍스트 리스트)
    private List<String> imageUrls; // 동화에 포함된 이미지들의 URL 리스트
    private String type;                 // PREMADE, FROM_PREMADE, USER_GENERATED
    private List<VocabResponseDto> words; // 이 동화에 등장하는 단어 리스트
    private String createdAt;            // 생성 시각
}