package com.owlearn.service;

import com.owlearn.dto.request.AddChildRequestDto;
import com.owlearn.dto.request.BuyItemRequestDto;
import com.owlearn.dto.request.DeleteChildrenRequestDto;
import com.owlearn.dto.request.SigninRequestDto;
import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.*;
import com.owlearn.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    NotifyResponseDto signup(SignupRequestDto signupRequestDto);

    SigninResponseDto signin(SigninRequestDto signinRequestDto);

    NotifyResponseDto modify(SignupRequestDto signupRequestDto);

    NotifyResponseDto checkId(String userId);

    NotifyResponseDto buyItem(String userId, Long childId, BuyItemRequestDto buyItemRequestDto);

    CharacterResponseDto getCharacter(Long childId, String userId);

    CharacterResponseDto uploadOrUpdateCharacter(Long childId, String userId, MultipartFile image);

    List<ChildResponseDto> getChildStatus(String userId);

    User getUserInfo(String userId);

    ChildIdResponseDto addChild(String userId, AddChildRequestDto addChildRequestDto);

    // 아이가 최근에 읽은 동화 반환
    ChildDetailResponseDto getChildDetail(Long childId, String userId);

    // 여러 자녀 삭제
    NotifyResponseDto deleteChildren(DeleteChildrenRequestDto req, String userId);

    ChildPreferBalanceResponseDto getBalance(Long childId, String userId);
}
