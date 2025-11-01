package com.owlearn.service;

import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.CharacterResponseDto;
import com.owlearn.dto.response.ChildStatusResponseDto;
import com.owlearn.dto.response.NotifyResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    NotifyResponseDto signup(SignupRequestDto signupRequestDto);
    NotifyResponseDto modify(SignupRequestDto signupRequestDto);
    NotifyResponseDto checkId(String userId);
    CharacterResponseDto getCharacter(String userId);
    CharacterResponseDto uploadOrUpdateCharacter(String userId, MultipartFile image);
    ChildStatusResponseDto getChildStatus(String userId);
}
