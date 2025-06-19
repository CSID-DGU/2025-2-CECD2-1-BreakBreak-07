package com.owlearn.service;

import com.owlearn.dto.QuizDto;
import com.owlearn.dto.request.QuizAnswerRequestDto;
import com.owlearn.dto.response.QuizAnswerResponseDto;
import com.owlearn.entity.Quiz;
import com.owlearn.entity.QuizSubmission;
import com.owlearn.entity.User;
import com.owlearn.repository.QuizRepository;
import com.owlearn.repository.QuizSubmissionRepository;
import com.owlearn.repository.TaleRepository;
import com.owlearn.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizSubmissionRepository submissionRepository;
    private final TaleRepository taleRepository;
    private final UserRepository userRepository;

    public QuizServiceImpl(QuizRepository quizRepository, QuizSubmissionRepository submissionRepository,
                       TaleRepository taleRepository, UserRepository userRepository) {
        this.quizRepository = quizRepository;
        this.submissionRepository = submissionRepository;
        this.taleRepository = taleRepository;
        this.userRepository = userRepository;
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
    public QuizAnswerResponseDto submitAnswer(QuizAnswerRequestDto request) {
        Quiz quiz = quizRepository.findByTaleIdAndQuestionNumber(request.getTaleId(), request.getQuestionNumber())
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈가 존재하지 않습니다."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        boolean isCorrect = quiz.getAnswerIndex() == request.getSelectedIndex();

        QuizSubmission submission = QuizSubmission.builder()
                .user(user)
                .tale(taleRepository.getReferenceById(request.getTaleId()))
                .questionNumber(request.getQuestionNumber())
                .selectedIndex(request.getSelectedIndex())
                .isCorrect(isCorrect)
                .build();

        submissionRepository.save(submission);

        return QuizAnswerResponseDto.builder()
                .isCorrect(isCorrect)
                .correctIndex(quiz.getAnswerIndex())
                .explanation(quiz.getExplanation())
                .selectedIndex(request.getSelectedIndex())
                .build();
    }

    @Override
    public QuizAnswerResponseDto getSubmission(Long userId, Long taleId, int questionNumber) {
        Quiz quiz = quizRepository.findByTaleIdAndQuestionNumber(taleId, questionNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈가 존재하지 않습니다."));

        QuizSubmission submission = submissionRepository.findByUserIdAndTaleIdAndQuestionNumber(userId, taleId, questionNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 퀴즈 제출 기록이 존재하지 않습니다."));

        return QuizAnswerResponseDto.builder()
                .isCorrect(submission.isCorrect())
                .correctIndex(quiz.getAnswerIndex())
                .explanation(quiz.getExplanation())
                .selectedIndex(submission.getSelectedIndex())
                .build();
        }

}