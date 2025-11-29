package com.owlearn.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class ItemStatusResponseDto {
    private Long itemId;
    private String name;
    private String category;
    private Boolean owned;
}
