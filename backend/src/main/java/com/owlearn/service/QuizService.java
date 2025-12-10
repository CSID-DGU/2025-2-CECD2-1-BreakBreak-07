package com.owlearn.service;

import com.owlearn.dto.QuizDto;
import com.owlearn.dto.request.QuizAnswerRequestDto;
import com.owlearn.dto.response.QuizAnswerResponseDto;

import java.util.List;

public interface QuizService {

    // 특정 동화(taleId)에 해당하는 퀴즈 목록을 QuizDto 형태로 반환
    List<QuizDto> getQuizzesByTaleId(Long taleId);

    // 퀴즈 정답 제출
    QuizAnswerResponseDto submitAnswer(QuizAnswerRequestDto request);

    // 퀴즈 정답 확인
    public QuizAnswerResponseDto getSubmission(Long userId, Long taleId, int questionNumber);
}

