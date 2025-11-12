package com.owlearn.dto.request;

import lombok.*;

import javax.security.auth.Subject;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaleCreateRequestDto {

    private String subject;
    private String tone;
    private String artStyle;
    private String ageGroup;
    private Long childId;
}
