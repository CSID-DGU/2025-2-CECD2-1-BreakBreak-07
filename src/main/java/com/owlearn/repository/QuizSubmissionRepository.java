package com.owlearn.repository;

import com.owlearn.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    Optional<QuizSubmission> findByUserIdAndTaleIdAndQuestionNumber(Long userId, Long taleId, int questionNumber);
}