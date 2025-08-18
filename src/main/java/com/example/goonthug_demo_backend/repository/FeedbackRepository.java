package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.Feedback;
import com.example.goonthug_demo_backend.model.GameAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Найти все фидбеки для конкретного назначения игры с загрузкой связанных данных
    @Query("SELECT f FROM Feedback f " +
           "JOIN FETCH f.gameAssignment ga " +
           "JOIN FETCH ga.game g " +
           "JOIN FETCH f.tester t " +
           "WHERE f.gameAssignment = :gameAssignment " +
           "ORDER BY f.createdAt DESC")
    List<Feedback> findByGameAssignmentOrderByCreatedAtDesc(@Param("gameAssignment") GameAssignment gameAssignment);

    // Найти все фидбеки для конкретной игры с загрузкой связанных данных
    @Query("SELECT f FROM Feedback f " +
           "JOIN FETCH f.gameAssignment ga " +
           "JOIN FETCH ga.game g " +
           "JOIN FETCH f.tester t " +
           "WHERE f.gameAssignment.game.id = :gameId " +
           "ORDER BY f.createdAt DESC")
    List<Feedback> findByGameIdOrderByCreatedAtDesc(@Param("gameId") Long gameId);

    // Найти все фидбеки от конкретного тестера с загрузкой связанных данных
    @Query("SELECT f FROM Feedback f " +
           "JOIN FETCH f.gameAssignment ga " +
           "JOIN FETCH ga.game g " +
           "JOIN FETCH f.tester t " +
           "WHERE f.tester.id = :testerId " +
           "ORDER BY f.createdAt DESC")
    List<Feedback> findByTesterIdOrderByCreatedAtDesc(@Param("testerId") Long testerId);

    // Найти финальный фидбек для конкретного назначения
    Optional<Feedback> findByGameAssignmentAndFeedbackType(GameAssignment gameAssignment, Feedback.FeedbackType feedbackType);

    // Проверить, есть ли уже финальный фидбек для данного назначения
    @Query("SELECT COUNT(f) > 0 FROM Feedback f WHERE f.gameAssignment = :gameAssignment AND f.feedbackType = 'FINAL'")
    boolean existsFinalFeedbackForAssignment(@Param("gameAssignment") GameAssignment gameAssignment);

    // Найти все фидбеки определенного типа
    List<Feedback> findByFeedbackTypeOrderByCreatedAtDesc(Feedback.FeedbackType feedbackType);
}
