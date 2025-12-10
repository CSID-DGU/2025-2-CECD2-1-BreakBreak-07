package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SigninRequestDto {
    private String userId;
    private String name;
    private String password;

}
