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
    private String characterImageUrl;
    private Integer credit;
}
