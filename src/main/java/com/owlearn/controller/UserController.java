package com.owlearn.controller;

import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.CharacterResponseDto;
import com.owlearn.dto.response.NotifyResponseDto;
import com.owlearn.dto.response.ResponseDto;
import com.owlearn.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")

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

    @GetMapping("/character")
    public ResponseDto<CharacterResponseDto> getCharacter(@RequestParam String userId) {
        return new ResponseDto<>(userService.getCharacter(userId));
    }

    @PostMapping(value = "/character", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<CharacterResponseDto> uploadCharacter(
            @RequestParam String userId,
            @RequestPart("image") MultipartFile image
    ) {
        return new ResponseDto<>(userService.uploadOrUpdateCharacter(userId, image));
    }
}
