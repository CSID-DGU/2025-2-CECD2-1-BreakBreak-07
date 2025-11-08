package com.owlearn.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageGenerateResponseDto {
    private List<String> urls;
}
