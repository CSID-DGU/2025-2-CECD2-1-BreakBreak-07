package com.owlearn.repository;

import com.owlearn.entity.Child;
import com.owlearn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {
    Optional<Child> findByUser(User user);

    // 소유권 검증 + 조회
    Optional<Child> findByIdAndUser_UserId(Long id, String userId);

    // 필요 시 소유권만 빠르게 확인
    boolean existsByIdAndUser_UserId(Long id, String userId);
}
