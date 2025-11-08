package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTaleRequestDto {
    Long taleId;
    Long userId;
}
