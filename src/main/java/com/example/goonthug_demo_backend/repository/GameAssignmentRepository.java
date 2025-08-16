package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.GameAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameAssignmentRepository extends JpaRepository<GameAssignment, Long> {
    boolean existsByGameIdAndTesterIdAndStatus(Long gameId, Long testerId, String status);

    // Находим конкретное назначение для тестера и игры
    @Query("SELECT ga FROM GameAssignment ga WHERE ga.game.id = :gameId AND ga.tester.id = :testerId AND ga.status = :status")
    Optional<GameAssignment> findByGameIdAndTesterIdAndStatus(@Param("gameId") Long gameId,
                                                              @Param("testerId") Long testerId,
                                                              @Param("status") String status);

    // Получаем все назначения для конкретной игры
    @Query("SELECT ga FROM GameAssignment ga WHERE ga.game.id = :gameId")
    List<GameAssignment> findByGameId(@Param("gameId") Long gameId);

    // Получаем все назначения для конкретного тестера
    @Query("SELECT ga FROM GameAssignment ga WHERE ga.tester.id = :testerId")
    List<GameAssignment> findByTesterId(@Param("testerId") Long testerId);

    // Получаем количество активных назначений для игры
    @Query("SELECT COUNT(ga) FROM GameAssignment ga WHERE ga.game.id = :gameId AND ga.status = :status")
    long countByGameIdAndStatus(@Param("gameId") Long gameId, @Param("status") String status);
}