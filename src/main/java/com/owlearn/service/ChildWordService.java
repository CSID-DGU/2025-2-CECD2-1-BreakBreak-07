// com.owlearn.service.ChildWordService

package com.owlearn.service;

import com.owlearn.dto.request.ChildWordSaveRequestDto;
import com.owlearn.dto.response.NotifyResponseDto;
import com.owlearn.dto.response.VocabDto;
import com.owlearn.dto.response.VocabResponseDto;
import com.owlearn.entity.Child;
import com.owlearn.entity.ChildWord;
import com.owlearn.exception.ApiException;
import com.owlearn.exception.ErrorDefine;
import com.owlearn.repository.ChildRepository;
import com.owlearn.repository.ChildWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ChildWordService {

    private final ChildRepository childRepository;
    private final ChildWordRepository childWordRepository;

    /**
     * 특정 자녀에 대해, 사용자가 "모르는 단어"로 체크한 단어들을 저장.
     * 이미 있는 단어는 무시하고 새로 들어온 것만 추가.
     */
    @Transactional
    public NotifyResponseDto saveUnknownWords(String userId, Long childId, ChildWordSaveRequestDto req) {

        // 1) 자녀가 로그인 유저의 자녀인지 검증
        Child child = childRepository.findByIdAndUser_UserId(childId, userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.CHILD_NOT_FOUND));

        // 2) 단어들 저장 (중복이면 스킵)
        req.getWords().forEach(dto -> {
            String word = dto.getWord().trim().toLowerCase();
            String meaning = dto.getMeaning().trim();

            if (word.isEmpty() || meaning.isEmpty()) return;

            boolean exists = childWordRepository.existsByChild_IdAndWordIgnoreCase(child.getId(), word);
            if (exists) {
                return; // 이미 있으면 패스
            }

            ChildWord entity = ChildWord.builder()
                    .child(child)
                    .word(word)
                    .meaning(meaning)
                    .build();

            childWordRepository.save(entity);
        });

        return NotifyResponseDto.builder().message("단어 리스트가 저장되었습니다.").build();
    }

    /**
     * 해당 자녀의 모든 단어장 목록 조회
     */
    @Transactional(readOnly = true)
    public List<VocabDto> getChildWords(String userId, Long childId) {
        // 자녀 소유 검증
        Child child = childRepository.findByIdAndUser_UserId(childId, userId)
                .orElseThrow(() -> new ApiException(ErrorDefine.CHILD_NOT_FOUND));

        return childWordRepository.findAllByChild_IdOrderByIdDesc(child.getId())
                .stream()
                .map(w -> VocabDto.builder()
                        .word(w.getWord())
                        .meaning(w.getMeaning())
                        .build())
                .collect(toList());
    }
}
