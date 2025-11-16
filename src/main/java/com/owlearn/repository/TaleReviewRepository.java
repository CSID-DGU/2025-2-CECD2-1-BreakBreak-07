package com.owlearn.repository;

import com.owlearn.entity.TaleReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaleReviewRepository extends JpaRepository<TaleReview, Long> {

    List<TaleReview> findByTaleId(Long taleId);

    List<TaleReview> findByChildId(Long childId);
}
