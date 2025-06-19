package com.owlearn.controller;

import com.owlearn.dto.QuizDto;
import com.owlearn.dto.request.QuizAnswerRequestDto;
import com.owlearn.dto.response.QuizAnswerResponseDto;
import com.owlearn.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * 특정 동화 ID(taleId)에 해당하는 퀴즈 리스트 조회 API
     * @param taleId 조회할 동화의 고유 ID
     * @return 해당 동화에 포함된 퀴즈 리스트 (QuizDto 리스트) 응답
     */
    @GetMapping("/{taleId}")
    public ResponseEntity<List<QuizDto>> getQuizzesByTale(@PathVariable Long taleId) {
        List<QuizDto> quizzes = quizService.getQuizzesByTaleId(taleId);
        return ResponseEntity.ok(quizzes);
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizAnswerResponseDto> submitQuizAnswer(@RequestBody QuizAnswerRequestDto request) {
        QuizAnswerResponseDto response = quizService.checkAnswer(request);
        return ResponseEntity.ok(response);
    }
}
