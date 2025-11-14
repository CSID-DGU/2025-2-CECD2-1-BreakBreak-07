package com.owlearn.controller;

import com.owlearn.config.JwtTokenProvider;
import com.owlearn.dto.request.AddChildRequestDto;
import com.owlearn.dto.request.SigninRequestDto;
import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.*;
import com.owlearn.entity.User;
import com.owlearn.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Security;
import java.util.List;

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

    @GetMapping("/me")
    public ResponseDto<User> getCurrentUserInfo() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(userService.getUserInfo(userId));
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

    @GetMapping("/character/{childId}")
    public ResponseDto<CharacterResponseDto> getCharacter(
            @PathVariable Long childId
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(userService.getCharacter(childId, userId));
    }

    @PostMapping(value = "/character", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<CharacterResponseDto> uploadCharacter(
            @RequestParam("childId") Long childId,
            @RequestPart("image") MultipartFile image
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(userService.uploadOrUpdateCharacter(childId, userId, image));
    }

    @GetMapping("/child")
    public ResponseDto<List<ChildResponseDto>> getChildStatus() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(userService.getChildStatus(userId));
    }

    @PostMapping("/child")
    public ResponseDto<ChildIdResponseDto> addChild(
            @RequestBody AddChildRequestDto addChildRequestDto
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(userService.addChild(userId, addChildRequestDto));
    }
}
