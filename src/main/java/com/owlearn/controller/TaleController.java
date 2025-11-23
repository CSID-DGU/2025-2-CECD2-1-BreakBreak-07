package com.owlearn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.owlearn.config.JwtTokenProvider;
import com.owlearn.dto.*;
import com.owlearn.dto.request.TaleCreateRequestDto;
import com.owlearn.dto.request.ChildTaleRequestDto;
import com.owlearn.dto.request.TaleOptionSearchRequestDto;
import com.owlearn.dto.response.*;
import com.owlearn.service.TaleAiService;
import com.owlearn.service.TaleService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tales")
public class TaleController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final TaleService taleService;
    private final TaleAiService taleAiService;
    private final ObjectMapper objectMapper;

    public TaleController(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            TaleService taleService,
            TaleAiService taleAiService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.taleService = taleService;
        this.taleAiService = taleAiService;
        this.objectMapper = new ObjectMapper();
    }

    // 기존 동화에 이미지 생성
    @PostMapping
    public ResponseDto<TaleIdResponseDto> generateImagesForExisting(@RequestBody ChildTaleRequestDto request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(taleAiService.generateImagesForExistingTale(userId, request));
    }

    // 새 동화 생성 + 이미지 생성
    @PostMapping("/generate")
    public ResponseDto<TaleIdResponseDto> createTaleAndGenerate(@RequestBody TaleCreateRequestDto request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto<>(taleAiService.createTaleAndGenerateImages(userId, request));
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
     * 기성동화(PREMADE) 삽입 API
     */
    @PostMapping(value = "/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto<TaleResponseDto> insertPremadeTale(
            @RequestBody TaleDto request
    ) {
        return new ResponseDto<>(new TaleResponseDto(taleService.insertTale(request)));
    }


    /**
     * 전체 동화 목록을 조회하는 API
     * @return 모든 동화의 상세 정보를 담은 리스트 응답 DTO
     */
    @GetMapping
    public ResponseDto<List<TaleSummaryResponseDto>> getAllTales() {
        return new ResponseDto<>(taleService.getAllTales());
    }

    /**
     * 기성동화(PREMADE)만 조회
     */
    @GetMapping("/premade")
    public ResponseDto<List<TaleSummaryResponseDto>> getPremadeTales() {
        return new ResponseDto<>(taleService.getPremadeTales());
    }

    /**
     * 생성동화(USER_GENERATED)만 조회
     */
    @GetMapping("/generated")
    public ResponseDto<List<TaleSummaryResponseDto>> getUserGeneratedTales() {
        return new ResponseDto<>(taleService.getUserGeneratedTales());
    }

    /**
     * 특정 옵션 동화 조회
     */
    @PostMapping("/options")
    public ResponseDto<List<TaleSummaryResponseDto>> getByOptions(
        @RequestBody TaleOptionSearchRequestDto requestDto
    ) {
        return new ResponseDto<>(taleService.getUserGeneratedTalesByOptions(requestDto));
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