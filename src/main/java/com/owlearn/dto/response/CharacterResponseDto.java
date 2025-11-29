package com.owlearn.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterResponseDto {
    private String imageUrl;
    private List<ItemStatusResponseDto> items;// 없으면 null
}