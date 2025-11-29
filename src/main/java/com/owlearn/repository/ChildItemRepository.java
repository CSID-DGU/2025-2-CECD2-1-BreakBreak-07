package com.owlearn.repository;

import com.owlearn.entity.ChildItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildItemRepository extends JpaRepository<ChildItem, Long> {
    List<ChildItem> findByChildId(Long childId);
}
