package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterResponseDto {
    private boolean exists;          // 캐릭터 이미지가 존재하는가?
    private String imageUrl;         // 없으면 null
}