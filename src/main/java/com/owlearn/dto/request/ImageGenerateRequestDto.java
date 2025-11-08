package com.owlearn.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageGenerateRequestDto {
    private List<String> prompts;
    private String refImgUrl;
}
