package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.GameAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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

    // Находим назначение для конкретного тестера и игры (любой статус)
    @Query("SELECT ga FROM GameAssignment ga WHERE ga.game.id = :gameId AND ga.tester.id = :testerId")
    Optional<GameAssignment> findByGameIdAndTesterId(@Param("gameId") Long gameId, @Param("testerId") Long testerId);

    // Новые методы для админ панели
    @Query("SELECT COUNT(ga) FROM GameAssignment ga WHERE ga.status = :status")
    Long countByStatus(@Param("status") String status);

    @Query("SELECT COUNT(ga) FROM GameAssignment ga WHERE ga.game.id = :gameId")
    Long countByGameId(@Param("gameId") Long gameId);

    @Query("SELECT COUNT(ga) FROM GameAssignment ga WHERE ga.tester.id = :testerId")
    Long countByTesterId(@Param("testerId") Long testerId);

    @Query("SELECT COUNT(ga) FROM GameAssignment ga WHERE ga.tester.id = :testerId AND ga.status = :status")
    Long countByTesterIdAndStatus(@Param("testerId") Long testerId, @Param("status") String status);

    @Query("SELECT COUNT(ga) FROM GameAssignment ga WHERE ga.game.company.id = :companyId")
    Long countByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT ga FROM GameAssignment ga " +
           "JOIN FETCH ga.game g " +
           "JOIN FETCH g.company " +
           "JOIN FETCH ga.tester " +
           "ORDER BY ga.assignedAt DESC")
    List<GameAssignment> findAllWithGameAndTester();

    @Query("SELECT ga FROM GameAssignment ga WHERE ga.tester.id = :testerId AND ga.status = :status")
    List<GameAssignment> findByTesterIdAndStatus(@Param("testerId") Long testerId, @Param("status") String status);

    @Query("SELECT ga FROM GameAssignment ga WHERE ga.game.id = :gameId AND ga.status = :status")
    List<GameAssignment> findByGameIdAndStatus(@Param("gameId") Long gameId, @Param("status") String status);
}