package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildDetailResponseDto {
    private Long id;
    private String name;
    private String preferSubject;
    private String preferTone;
    private String preferStyle;
    private String preferAge;
    private String characterImageUrl;
    private Integer taleCount;
    private Integer credit;
}
