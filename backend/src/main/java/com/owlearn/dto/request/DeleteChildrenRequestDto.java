package com.owlearn.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteChildrenRequestDto {

    private List<Long> childIds;
}

