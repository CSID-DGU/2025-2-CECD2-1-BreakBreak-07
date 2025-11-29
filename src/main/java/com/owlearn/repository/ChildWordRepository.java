package com.owlearn.repository;

import com.owlearn.entity.ChildWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildWordRepository extends JpaRepository<ChildWord, Long> {

    boolean existsByChild_IdAndWordIgnoreCase(Long childId, String word);

    List<ChildWord> findAllByChild_IdOrderByIdDesc(Long childId);
}