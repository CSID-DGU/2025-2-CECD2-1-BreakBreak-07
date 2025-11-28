package com.owlearn.service;

import com.owlearn.config.JwtTokenProvider;
import com.owlearn.dto.request.AddChildRequestDto;
import com.owlearn.dto.request.BuyItemRequestDto;
import com.owlearn.dto.request.SigninRequestDto;
import com.owlearn.dto.request.SignupRequestDto;
import com.owlearn.dto.response.*;
import com.owlearn.entity.Child;
import com.owlearn.entity.ChildItem;
import com.owlearn.entity.Item;
import com.owlearn.entity.User;
import com.owlearn.exception.ApiException;
import com.owlearn.exception.ErrorDefine;
import com.owlearn.repository.*;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LocalImageStorage imageStorage;
    private final ChildRepository childRepository;
    private final TaleRepository taleRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ItemRepository itemRepository;
    private final ChildItemRepository childItemRepository;

    public UserServiceImpl(UserRepository userRepository, LocalImageStorage localImageStorage, ChildRepository childRepository, TaleRepository taleRepository, JwtTokenProvider jwtTokenProvider, ItemRepository itemRepository, ChildItemRepository childItemRepository) {
        this.userRepository = userRepository;
        this.imageStorage = localImageStorage;
        this.childRepository = childRepository;
        this.taleRepository = taleRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.itemRepository = itemRepository;
        this.childItemRepository = childItemRepository;
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
    @Override
    public SigninResponseDto signin(SigninRequestDto signinRequestDto) {
        User user = userRepository.findByUserId(signinRequestDto.getUserId())
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        if (!user.getPassword().equals(signinRequestDto.getPassword())) {
            throw new ApiException(ErrorDefine.ACCESS_DENIED);
        }

        // JWT 토큰 발행
        String token = jwtTokenProvider.generateToken(user.getUserId());

        // 4응답 반환
        return SigninResponseDto.builder()
                .message("로그인 성공")
                .token(token) // NotifyResponseDto에 token 필드 추가 가능
                .role(user.getRole())
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
    public CharacterResponseDto getCharacter(Long childId, String userId) {

        Child child = childRepository.findByIdAndUser_UserId(childId, userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.ACCESS_DENIED));

        List<Item> allItems = itemRepository.findAll();

        List<ChildItem> childItems = childItemRepository.findByChildId(childId);

        Map<Long, Boolean> ownedMap = childItems.stream()
                .collect(Collectors.toMap(
                        ci -> ci.getItem().getId(),
                        ChildItem::getOwned
                ));

        List<ItemStatusResponseDto> itemStatusList = allItems.stream()
                .map(item -> ItemStatusResponseDto.builder()
                        .itemId(item.getId())
                        .name(item.getName())
                        .category(item.getCategory())
                        .owned(ownedMap.getOrDefault(item.getId(), false)) // 없으면 false
                        .build()
                )
                .toList();

        return CharacterResponseDto.builder()
                .imageUrl(child.getCharacterImageUrl())
                .items(itemStatusList)
                .build();
    }


    @Override
    public CharacterResponseDto uploadOrUpdateCharacter(Long childId, String userId, MultipartFile image) {
        Child child = childRepository.findByIdAndUser_UserId(childId, userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.ACCESS_DENIED));

        try {
            String url = imageStorage.saveUserCharacterImage(childId, image);
            child.setCharacterImageUrl(url);
            childRepository.save(child);

            return CharacterResponseDto.builder()
                    .imageUrl(url)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("캐릭터 이미지 저장 중 오류가 발생했습니다.", e);
        }
    }

    public List<ChildResponseDto> getChildStatus(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));
        List<Child> children = childRepository.findAllByUser(user);

        return children.stream()
                .map(child -> ChildResponseDto.builder()
                        .id(child.getId())
                        .name(child.getName())
                        .age(child.getAge())
                        .prefer(child.getPrefer())
                        .characterImageUrl(child.getCharacterImageUrl())
                        .taleCount(taleRepository.countByChild(child))
                        .build())
                .toList();
    }

    public ChildIdResponseDto addChild(String userId, AddChildRequestDto addChildRequestDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));
        Child child = Child.builder()
                .name(addChildRequestDto.getChildName())
                .age(addChildRequestDto.getAge())
                .user(user)
                .build();
        childRepository.save(child);

        return ChildIdResponseDto.builder().childId(child.getId()).build();
    }

    @Override
    public User getUserInfo(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.USER_NOT_FOUND));

        return user;
    }

    @Override
    public ChildDetailResponseDto getChildDetail(Long childId, String userId) {
        Child child = childRepository.findByIdAndUser_UserId(childId, userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.ACCESS_DENIED));

        return ChildDetailResponseDto.builder()
                .id(child.getId())
                .name(child.getName())
                .characterImageUrl(child.getCharacterImageUrl())
                .credit(child.getCredit())
                .build();
    }

    @Override
    @Transactional
    public NotifyResponseDto buyItem(String userId, Long childId, BuyItemRequestDto buyItemRequestDto) {

        Child child = childRepository.findByIdAndUser_UserId(childId, userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.ACCESS_DENIED));

        Long itemId = buyItemRequestDto.getItemId();
        Integer price = buyItemRequestDto.getPrice();

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(ErrorDefine.ITEM_NOT_FOUND));

        if (child.getCredit() < price) {
            throw new ApiException(ErrorDefine.NOT_ENOUGH_CREDIT);
        }

        child.setCredit(child.getCredit() - price);
        childRepository.save(child);

        ChildItem childItem = ChildItem.builder()
                .child(child)
                .item(item)
                .build();

        childItemRepository.save(childItem);

        return new NotifyResponseDto("아이템 구매 완료!");
    }

}
