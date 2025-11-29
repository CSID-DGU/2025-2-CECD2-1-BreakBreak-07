// com.owlearn.controller.ChildWordController

package com.owlearn.controller;

import com.owlearn.dto.request.ChildWordSaveRequestDto;
import com.owlearn.dto.response.NotifyResponseDto;
import com.owlearn.dto.response.ResponseDto;
import com.owlearn.dto.response.VocabResponseDto;
import com.owlearn.service.ChildWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/child/{childId}/words")
public class ChildWordController {

    private final ChildWordService childWordService;

    /**
     * 단어 저장 (사용자가 모르는 단어로 체크한 리스트 저장)
     */
    @PostMapping
    public ResponseDto<NotifyResponseDto> saveWords(
            Principal principal,
            @PathVariable Long childId,
            @RequestBody ChildWordSaveRequestDto req
    ) {
        String userId = principal.getName(); // JWT 기반 로그인이라면 이런 식으로 userId 꺼내겠지
        return new ResponseDto<>(childWordService.saveUnknownWords(userId, childId, req));
    }

    /**
     * 해당 자녀의 모든 단어 조회
     */
    @GetMapping
    public ResponseDto<List<VocabResponseDto>> getWords(
            Principal principal,
            @PathVariable Long childId
    ) {
        String userId = principal.getName();
        return new ResponseDto<>(childWordService.getChildWords(userId, childId));

    }
}
