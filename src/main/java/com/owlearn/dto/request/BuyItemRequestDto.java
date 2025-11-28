package com.owlearn.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class BuyItemRequestDto {
    private Long itemId;
    private Integer price;
}
