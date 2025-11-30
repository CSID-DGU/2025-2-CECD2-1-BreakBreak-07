package com.owlearn.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildPreferBalanceResponseDto {
    private Double avgFriendship;
    private Double avgFamily;
    private Double avgGrowth;
    private Double avgAdventure;
}
