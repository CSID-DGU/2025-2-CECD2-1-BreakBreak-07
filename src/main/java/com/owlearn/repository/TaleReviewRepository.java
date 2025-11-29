package com.owlearn.repository;

import com.owlearn.entity.TaleReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaleReviewRepository extends JpaRepository<TaleReview, Long> {

    List<TaleReview> findByTaleId(Long taleId);

    List<TaleReview> findByChildId(Long childId);

    @Query("""
        SELECT r.tale.id, AVG(r.rating)
        FROM TaleReview r
        WHERE r.tale.id IN :taleIds
        GROUP BY r.tale.id
    """)
    List<Object[]> findAvgScoreByTaleIds(@Param("taleIds") List<Long> taleIds);

    int countByChildId(Long childId);
}
