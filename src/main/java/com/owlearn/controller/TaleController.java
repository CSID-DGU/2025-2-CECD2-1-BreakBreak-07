package com.owlearn.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.owlearn.dto.*;
import com.owlearn.dto.request.TaleCreateRequestDto;
import com.owlearn.dto.response.ResponseDto;
import com.owlearn.dto.response.TaleDetailResponseDto;
import com.owlearn.dto.response.TaleResponseDto;
import com.owlearn.dto.response.TaleSummaryResponseDto;
import com.owlearn.service.TaleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/tales")
public class TaleController {

    private final TaleService taleService;
    private final ObjectMapper objectMapper;

    public TaleController(TaleService taleService) {
        this.taleService = taleService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 새로운 동화를 생성하는 API
     * @param request 동화 생성 요청 DTO
     * @return 생성된 동화의 ID를 담은 응답 DTO
     */
    @PostMapping
    public ResponseEntity<TaleResponseDto> createTale(@RequestBody TaleCreateRequestDto request) {
        Long taleId = taleService.createTale(request);
        return ResponseEntity.ok(new TaleResponseDto(taleId));
    }

    /**
     * 특정 동화 ID에 해당하는 동화 상세 조회 API
     * @param taleId 조회할 동화의 고유 ID
     * @return 동화 상세 정보를 담은 응답 DTO
     */
    @GetMapping("/{taleId}")
    public ResponseDto<TaleDetailResponseDto> getTale(@PathVariable Long taleId) {
        return new ResponseDto<>(taleService.getTale(taleId));
    }

    /**
     * 동화 직접 삽입 API (이미지 파일 업로드 포함)
     * @param title 동화 제목
     * @param contents 동화 내용 리스트
     * @param quizzesJson JSON 문자열
     * @param images 동화에 포함될 이미지 파일들 (multipart/form-data)
     * @return 생성된 동화의 ID를 포함한 응답 DTO
     */
    @PostMapping(value = "/insert", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

    public ResponseEntity<TaleResponseDto> insertTale(

            @RequestParam String title,
            @RequestParam List<String> contents,
            @RequestParam String quizzesJson,
            @RequestPart("images") List<MultipartFile> images) {

        // images 파일들을 서버 static 폴더에 저장하고 저장된 url 리스트 생성
        List<String> savedImageUrls = taleService.saveImages(images);

        // quizzesJson을 List<QuizDto>로 변환
        List<QuizDto> quizzes;
        try {
            quizzes = objectMapper.readValue(quizzesJson, new TypeReference<List<QuizDto>>() {});
        } catch (JsonProcessingException e) {
            // JSON 파싱 실패
            e.printStackTrace();
            throw new RuntimeException("Invalid quizzes JSON format", e);
        }

        // TaleDto 생성
        TaleDto taleDto = TaleDto.builder()
                .title(title)
                .contents(contents)
                .imageUrls(savedImageUrls)
                .quizzes(quizzes)
                .build();

        // DB에 저장
        Long taleId = taleService.insertTale(taleDto);

        return ResponseEntity.ok(new TaleResponseDto(taleId));

    }

    /**
     * 전체 동화 목록을 조회하는 API
     * @return 모든 동화의 상세 정보를 담은 리스트 응답 DTO
     */
    @GetMapping
    public ResponseEntity<List<TaleSummaryResponseDto>> getAllTales() {
        return ResponseEntity.ok(taleService.getAllTales());
    }

    /**
     * 특정 동화를 수정하는 API
     * @param taleId 수정할 동화의 고유 ID
     * @param request 수정할 동화 데이터를 담은 요청 DTO
     * @return 수정된 동화의 상세 정보를 담은 응답 DTO
     */
    @PutMapping("/{taleId}")
    public ResponseEntity<TaleDto> updateTale(@PathVariable Long taleId,
                                              @RequestBody TaleDto request) {
        return ResponseEntity.ok(taleService.updateTale(taleId, request));
    }

    /**
     * 특정 동화를 삭제하는 API
     * @param taleId 삭제할 동화의 고유 ID
     * @return 성공적으로 삭제된 경우, 204 No Content 응답 반환
     */
    @DeleteMapping("/{taleId}")
    public ResponseEntity<Void> deleteTale(@PathVariable Long taleId) {
        taleService.deleteTale(taleId);
        return ResponseEntity.noContent().build();
    }
}