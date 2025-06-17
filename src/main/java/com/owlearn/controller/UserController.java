package com.owlearn.controller;

import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.NotifyResponseDto;
import com.owlearn.dto.response.ResponseDto;
import com.owlearn.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping

public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseDto<NotifyResponseDto> signup(
            @RequestBody SignupRequestDto signupRequestDto
    ){
        return new ResponseDto<>(userService.signup(signupRequestDto));
    }
}
