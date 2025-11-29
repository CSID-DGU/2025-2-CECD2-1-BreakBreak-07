// com.owlearn.dto.request.ChildWordSaveRequestDto

package com.owlearn.dto.request;

import com.owlearn.dto.response.VocabResponseDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ChildWordSaveRequestDto {

    @NotEmpty
    private List<VocabResponseDto> words;   // 프론트에서 체크 후 보낸 단어들

}
