package com.owlearn.service;

import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.NotifyResponseDto;

public interface UserService {
    NotifyResponseDto signup(SignupRequestDto signupRequestDto);
    NotifyResponseDto modify(SignupRequestDto signupRequestDto);
    NotifyResponseDto checkId(String userId);
}
