package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildStatusResponseDto {
    private String name;
    private Integer taleCount;
    private String prefer;
}
