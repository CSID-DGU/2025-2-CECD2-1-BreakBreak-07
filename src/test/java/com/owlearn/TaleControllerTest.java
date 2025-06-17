package com.owlearn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owlearn.controller.TaleController;
import com.owlearn.dto.request.TaleCreateRequestDto;
import com.owlearn.dto.response.TaleDetailResponseDto;
import com.owlearn.service.TaleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaleController.class)
public class TaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaleService taleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("동화 생성 요청이 성공하면 id를 반환한다")
    void testCreateTale() throws Exception {
        // given
        TaleCreateRequestDto request = TaleCreateRequestDto.builder()
                .topic("공룡")
                .style("픽사 스타일")
                .age(6)
                .build();

        Mockito.when(taleService.createTale(any())).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/api/tales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("동화 조회 요청이 성공하면 내용과 이미지 URL을 반환한다")
    void testGetTale() throws Exception {
        // given
        TaleDetailResponseDto response = TaleDetailResponseDto.builder()
                .title("공룡 이야기")
                .contents(Arrays.asList("내용1", "내용2", "내용3"))
                .imageUrls(Arrays.asList("image1.jpg", "image2.jpg", "image3.jpg"))
                .build();

        Mockito.when(taleService.getTale(eq(1L))).thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/tales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("공룡 이야기"))
                .andExpect(jsonPath("$.contents.length()").value(3))
                .andExpect(jsonPath("$.imageUrls.length()").value(3));
    }
}
