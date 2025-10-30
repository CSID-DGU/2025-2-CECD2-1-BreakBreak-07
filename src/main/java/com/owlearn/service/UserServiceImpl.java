package com.owlearn.service;

import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.CharacterResponseDto;
import com.owlearn.dto.response.NotifyResponseDto;
import com.owlearn.entity.User;
import com.owlearn.exception.ApiException;
import com.owlearn.exception.ErrorDefine;
import com.owlearn.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LocalImageStorage imageStorage;

    public UserServiceImpl(UserRepository userRepository, LocalImageStorage localImageStorage) {
        this.userRepository = userRepository;
        this.imageStorage = localImageStorage;
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

    @Override
    public CharacterResponseDto getCharacter(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return CharacterResponseDto.builder()
                .exists(user.getCharacterImageUrl() != null)
                .imageUrl(user.getCharacterImageUrl())
                .build();
    }

    @Override
    public CharacterResponseDto uploadOrUpdateCharacter(String userId, MultipartFile image) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        try {
            String url = imageStorage.saveUserCharacterImage(userId, image);
            user.setCharacterImageUrl(url);
            userRepository.save(user);

            return CharacterResponseDto.builder()
                    .exists(true)
                    .imageUrl(url)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("캐릭터 이미지 저장 중 오류가 발생했습니다.", e);
        }
    }


}
