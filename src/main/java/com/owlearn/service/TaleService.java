package com.owlearn.service;

import com.owlearn.dto.TaleDto;

import com.owlearn.dto.request.TaleCreateRequestDto;
import com.owlearn.dto.response.TaleDetailResponseDto;
import com.owlearn.dto.response.TaleResponseDto;
import com.owlearn.dto.response.TaleSummaryResponseDto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaleService {

    // 특정 ID에 해당하는 동화 상세 정보를 반환
    TaleDetailResponseDto getTale(Long taleId);

    // 동화를 삽입하고, 삽입한 동화의 ID를 반환
    Long insertTale(TaleDto request);

    // 모든 동화의 상세 정보를 리스트로 반환
    List<TaleSummaryResponseDto> getAllTales();

    // 특정 ID에 해당하는 동화 정보를 수정하고, 수정된 결과를 반환
    TaleDto updateTale(Long taleId, TaleDto request);

    // 특정 ID에 해당하는 동화를 삭제
    void deleteTale(Long taleId);

    // images 파일들을 서버 static 폴더에 저장하고 저장된 url 리스트 반환
    List<String> saveImages(List<MultipartFile> images);

}