package com.owlearn.controller;

import com.owlearn.dto.request.TaleReviewCreateRequestDto;
import com.owlearn.dto.response.ResponseDto;
import com.owlearn.dto.response.TaleReviewResponseDto;
import com.owlearn.service.TaleReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class TaleReviewController {

    private final TaleReviewService taleReviewService;

    /**
     * 독후감 작성
     */
    @PostMapping("/child/{childId}/tales/{taleId}")
    public ResponseDto<TaleReviewResponseDto> createReview(
            @PathVariable Long childId,
            @PathVariable Long taleId,
            @RequestBody TaleReviewCreateRequestDto req
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(taleReviewService.createReview(userId, childId, taleId, req));
    }

    /**
     * 독후감 단건 조회
     */
    @GetMapping("/{reviewId}")
    public ResponseDto<TaleReviewResponseDto> getReviewById(
            @PathVariable Long reviewId
    ) {
        String userId = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        TaleReviewResponseDto dto = taleReviewService.getReviewById(userId, reviewId);
        return new ResponseDto<>(dto);
    }

    /**
     * 특정 동화에 대한 독후감 조회
     */
    @GetMapping("/tales/{taleId}")
    public ResponseDto<List<TaleReviewResponseDto>> getReviewsByTale(
            @PathVariable Long taleId
    ) {
        return new ResponseDto<>(taleReviewService.getReviewsByTale(taleId));
    }

    /**
     * 특정 아이의 독후감 목록 조회
     */
    @GetMapping("/child/{childId}")
    public ResponseDto<List<TaleReviewResponseDto>> getReviewsByChild(
            @PathVariable Long childId
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(taleReviewService.getReviewsByChild(userId, childId));
    }

}
