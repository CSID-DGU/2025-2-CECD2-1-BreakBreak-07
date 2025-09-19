package com.owlearn.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiRequestDto {
    String prompt;
    private MultipartFile refImage;
}
