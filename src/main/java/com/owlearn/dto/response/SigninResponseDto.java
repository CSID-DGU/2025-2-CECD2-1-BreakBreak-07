package com.owlearn.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class SigninResponseDto {
    private String message;
    private String token;
    private String role;
    private String name;
}
