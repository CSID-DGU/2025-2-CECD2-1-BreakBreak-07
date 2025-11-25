package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PremadeTaleResponseDto {

    private Long id;
    private String title;
    private String type;
    private String subject;
    private String tone;
    private String artStyle;
    private String ageGroup;
}