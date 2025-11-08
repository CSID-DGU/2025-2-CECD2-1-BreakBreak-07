package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextGenerateRequestDto {
    private String subject;
    private String tone;
    private String artStyle;
    private String ageGroup;
}
