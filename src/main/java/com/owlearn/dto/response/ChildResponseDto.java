package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildResponseDto {
    private Long id;
    private String name;
    private Integer age;
    private String preferSubject;
    private String preferTone;
    private String preferStyle;
    private String preferAge;
    private Integer taleCount;
}
