package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleOptionSearchRequestDto {
    private String subject;
    private String tone;
    private String artStyle;
    private String ageGroup;
}
