package com.owlearn.controller;

import com.owlearn.config.JwtTokenProvider;
import com.owlearn.dto.request.SigninRequestDto;
import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.*;
import com.owlearn.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")

public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public UserController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseDto<NotifyResponseDto> signup(
            @RequestBody SignupRequestDto signupRequestDto
    ){
        return new ResponseDto<>(userService.signup(signupRequestDto));
    }

    @PostMapping("/signin")
    public ResponseDto<SigninResponseDto> signin(
            @RequestBody SigninRequestDto signinRequestDto
    ){
        return new ResponseDto<>(userService.signin(signinRequestDto));

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
    public ResponseDto<CharacterResponseDto> getCharacter() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(userService.getCharacter(userId));
    }

    @PostMapping(value = "/character", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<CharacterResponseDto> uploadCharacter(
            @RequestParam String userId,
            @RequestPart("image") MultipartFile image
    ) {
        return new ResponseDto<>(userService.uploadOrUpdateCharacter(userId, image));
    }

    @GetMapping("/child")
    public ResponseDto<ChildStatusResponseDto> getChildStatus() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(userService.getChildStatus(userId));
    }
}
