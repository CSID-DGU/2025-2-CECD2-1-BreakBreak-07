package com.owlearn.dto.response;

import com.owlearn.dto.ChildWordDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildWordResponseDto {
    List<ChildWordDto> words; // 단어 리스트
    int count; // 단어 개수
}
