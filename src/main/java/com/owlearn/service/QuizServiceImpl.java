package com.owlearn.service;

import com.owlearn.dto.QuizDto;
import com.owlearn.dto.request.QuizAnswerRequestDto;
import com.owlearn.dto.response.QuizAnswerResponseDto;
import com.owlearn.entity.Quiz;
import com.owlearn.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    public List<QuizDto> getQuizzesByTaleId(Long taleId) {
        return quizRepository.findByTaleId(taleId).stream()
                .map(q -> new QuizDto(
                        q.getQuestionNumber(),
                        q.getQuestion(),
                        q.getChoices(),
                        q.getAnswerIndex(),
                        q.getExplanation()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public QuizAnswerResponseDto checkAnswer(QuizAnswerRequestDto request) {
        Quiz quiz = quizRepository.findByTaleIdAndQuestionNumber(request.getTaleId(), request.getQuestionNumber())
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈가 존재하지 않습니다."));

        boolean isCorrect = (quiz.getAnswerIndex() == request.getSelectedIndex());

        return new QuizAnswerResponseDto(isCorrect, quiz.getAnswerIndex(), quiz.getExplanation());
    }
}