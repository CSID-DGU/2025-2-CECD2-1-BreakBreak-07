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

    // Subject 옵션별 평균 평점 계산 쿼리
    @Query("SELECT t.subject, AVG(r.rating) " +
            "FROM TaleReview r JOIN r.tale t " + // <--- r.tale을 통해 t에 접근
            "WHERE r.child.id = :childId " +
            "GROUP BY t.subject")
    List<Object[]> findAverageRatingBySubject(@Param("childId") Long childId);

    // Tone 옵션별 평균 평점 계산 쿼리
    @Query("SELECT t.tone, AVG(r.rating) " +
            "FROM TaleReview r JOIN r.tale t " + // <--- r.tale을 통해 t에 접근
            "WHERE r.child.id = :childId " +
            "GROUP BY t.tone")
    List<Object[]> findAverageRatingByTone(@Param("childId") Long childId);

    @Query("SELECT t.artStyle, AVG(r.rating) " +
            "FROM TaleReview r JOIN r.tale t " + // <--- r.tale을 통해 t에 접근
            "WHERE r.child.id = :childId " +
            "GROUP BY t.artStyle")
    List<Object[]> findAverageRatingByArtStyle(@Param("childId") Long childId);

    @Query("SELECT t.ageGroup, AVG(r.rating) " +
            "FROM TaleReview r JOIN r.tale t " + // <--- r.tale을 통해 t에 접근
            "WHERE r.child.id = :childId " +
            "GROUP BY t.ageGroup")
    List<Object[]> findAverageRatingByAgeGroup(@Param("childId") Long childId);
    int countByChildId(Long childId);
}
