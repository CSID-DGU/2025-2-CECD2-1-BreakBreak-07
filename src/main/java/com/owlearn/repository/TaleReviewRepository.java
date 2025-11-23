package com.owlearn.repository;

import com.owlearn.entity.TaleReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaleReviewRepository extends JpaRepository<TaleReview, Long> {

    List<TaleReview> findByTaleId(Long taleId);

    List<TaleReview> findByChildId(Long childId);

    Optional<TaleReview> findByChildIdAndTaleId(Long childId, Long taleId);

    int countByChildId(Long childId);
}
