package com.owlearn.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextGenerateResponseDto {
    private String title;
    private List<String> contents;
}
