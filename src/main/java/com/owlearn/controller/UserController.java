package com.owlearn.controller;

import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.NotifyResponseDto;
import com.owlearn.dto.response.ResponseDto;
import com.owlearn.service.UserService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")

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
    @PutMapping("/modify")
    public ResponseDto<NotifyResponseDto> modify(
            @RequestBody SignupRequestDto signupRequestDto
    ){
        return new ResponseDto<>(userService.modify(signupRequestDto));
    }

    @GetMapping("/checkId")
    public ResponseDto<NotifyResponseDto> checkId(
            @RequestParam String userId
    ){
        return new ResponseDto<>(userService.checkId(userId));
    }
}
