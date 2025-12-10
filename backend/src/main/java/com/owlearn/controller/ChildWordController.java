// com.owlearn.controller.ChildWordController

package com.owlearn.controller;

import com.owlearn.dto.ChildWordDto;
import com.owlearn.dto.response.ChildWordResponseDto;
import com.owlearn.dto.response.ResponseDto;
import com.owlearn.dto.response.VocabResponseDto;
import com.owlearn.service.ChildWordService;
import com.owlearn.service.TaleAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/child/{childId}/words")
public class ChildWordController {

    private final ChildWordService childWordService;
    private final TaleAiService taleAiService;

    /**
     * 단어 뜻 요청 & 저장
     */
    @PostMapping
    public ResponseDto<List<VocabResponseDto>> saveWords(
            @PathVariable Long childId,
            @RequestBody List<String> req
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<VocabResponseDto> words = taleAiService.getWordMeaning(req);
        childWordService.saveUnknownWords(userId, childId, words);
        return new ResponseDto<>(words);
    }

    /**
     * 해당 자녀의 모든 단어 조회
     */
    @GetMapping
    public ResponseDto<ChildWordResponseDto> getWords(
            @PathVariable Long childId
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(childWordService.getChildWords(userId, childId));

    }
}
