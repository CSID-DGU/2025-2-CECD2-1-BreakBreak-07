package com.owlearn.service;

import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.NotifyResponseDto;
import com.owlearn.entity.User;
import com.owlearn.exception.ApiException;
import com.owlearn.exception.ErrorDefine;
import com.owlearn.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public NotifyResponseDto signup(SignupRequestDto signupRequestDto) {
        if (userRepository.findByUserId(signupRequestDto.getUserId()).isPresent())
            throw new ApiException(ErrorDefine.USER_EXIST);
        User user = User.builder()
                .userId(signupRequestDto.getUserId())
                .name(signupRequestDto.getName())
                .password(signupRequestDto.getPassword())
                .build();
        userRepository.save(user);
        return NotifyResponseDto.builder()
                .message("회원가입이 잘 되었습니다")
                .build();
    }

    public NotifyResponseDto modify(SignupRequestDto signupRequestDto) {
        if (userRepository.findByUserId(signupRequestDto.getUserId()).isPresent())
            throw new ApiException(ErrorDefine.USER_EXIST);
        User user = User.builder()
                .userId(signupRequestDto.getUserId())
                .name(signupRequestDto.getName())
                .password(signupRequestDto.getPassword())
                .build();
        userRepository.save(user);
        return NotifyResponseDto.builder()
                .message("회원정보가 수정되었습니다")
                .build();
    }

    public NotifyResponseDto checkId(String userId) {
        boolean exists = userRepository.findByUserId(userId).isPresent();
        String message = exists ? "중복된 아이디입니다." : "사용가능한 아이디입니다.";
        return NotifyResponseDto.builder()
                .message(message)
                .build();
    }


}
