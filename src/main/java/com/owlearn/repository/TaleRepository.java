package com.owlearn.repository;

import com.owlearn.entity.Child;
import com.owlearn.entity.Tale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaleRepository extends JpaRepository<Tale, Long> {

    @Query("SELECT COUNT(t) FROM Tale t WHERE t.child = :child")
    Integer countByChild(@Param("child") Child child);

    List<Tale> findByType(Tale.TaleType type);

    List<Tale> findByTypeAndSubjectAndToneAndArtStyleAndAgeGroup(
            Tale.TaleType type,
            String subject,
            String tone,
            String artStyle,
            String ageGroup
    );



}