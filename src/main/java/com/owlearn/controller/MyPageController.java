package com.owlearn.controller;

import com.owlearn.dto.response.*;
import com.owlearn.service.TaleReviewService;
import com.owlearn.service.TaleService;
import com.owlearn.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final UserService userService;
    private final TaleService taleService;
    private final TaleReviewService taleReviewService;

    @GetMapping("/{childId}")
    public ResponseDto<MyPageResponseDto> getMyPage(
            @PathVariable Long childId
    ) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        ChildDetailResponseDto child = userService.getChildDetail(childId, userId);
        TaleSummaryResponseDto recentTale = taleService.getRecentTaleByChildId(childId, userId);
        ReportSummaryDto reportSummary = taleReviewService.getReportSummaryByChildId(childId, userId);

        MyPageResponseDto myPage = MyPageResponseDto.builder()
                .child(child)
                .recentTale(recentTale)
                .reportSummary(reportSummary)
                .build();

        return new ResponseDto<>(myPage);
    }
}
