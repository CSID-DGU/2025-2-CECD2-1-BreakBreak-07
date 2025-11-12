package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildTaleRequestDto {
    Long taleId;
    Long childId;
}
