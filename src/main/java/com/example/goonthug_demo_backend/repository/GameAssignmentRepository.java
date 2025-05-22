package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.GameAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameAssignmentRepository extends JpaRepository<GameAssignment, Long> {
    boolean existsByGameIdAndStatus(Long gameId, String status);
    boolean existsByGameIdAndTesterIdAndStatus(Long gameId, Long testerId, String status);
}