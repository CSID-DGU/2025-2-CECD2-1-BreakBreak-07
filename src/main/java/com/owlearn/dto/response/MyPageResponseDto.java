package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseDto {

    private ChildDetailResponseDto child;
    private TaleSummaryResponseDto recentTale;
    private ChildPreferBalanceResponseDto balance;
    private ReportSummaryDto reportSummary;
}