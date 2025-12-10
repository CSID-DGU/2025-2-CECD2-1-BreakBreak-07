package com.owlearn.repository;

import com.owlearn.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // 특정 Tale의 모든 퀴즈 조회
    List<Quiz> findByTaleId(Long taleId);

    // 툭정 Tale의 특정 Quiz 조회
    Optional<Quiz> findByTaleIdAndQuestionNumber(Long taleId, int questionNumber);
}