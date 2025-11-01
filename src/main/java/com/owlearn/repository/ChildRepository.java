package com.owlearn.repository;

import com.owlearn.entity.Child;
import com.owlearn.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {
    Optional<Child> findByUser(User user);
}
