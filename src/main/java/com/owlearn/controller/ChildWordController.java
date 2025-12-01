// com.owlearn.controller.ChildWordController

package com.owlearn.controller;

import com.owlearn.dto.request.ChildWordSaveRequestDto;
import com.owlearn.dto.response.NotifyResponseDto;
import com.owlearn.dto.response.ResponseDto;
import com.owlearn.dto.response.VocabDto;
import com.owlearn.dto.response.VocabResponseDto;
import com.owlearn.service.ChildWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/child/{childId}/words")
public class ChildWordController {

    private final ChildWordService childWordService;

    /**
     * 단어 뜻 요청 & 저장
     */
    @PostMapping
    public ResponseDto<NotifyResponseDto> saveWords(
            @PathVariable Long childId,
            @RequestBody ChildWordSaveRequestDto req
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(childWordService.saveUnknownWords(userId, childId, req));
    }

    /**
     * 해당 자녀의 모든 단어 조회
     */
    @GetMapping
    public ResponseDto<List<VocabDto>> getWords(
            @PathVariable Long childId
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(childWordService.getChildWords(userId, childId));

    }
}
