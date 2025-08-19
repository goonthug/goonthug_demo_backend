package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.dto.*;
import com.example.goonthug_demo_backend.model.*;
import com.example.goonthug_demo_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameAssignmentRepository gameAssignmentRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    public AdminDashboardStatsDTO getDashboardStats() {
        Long totalGames = gameRepository.count();
        Long totalUsers = userRepository.count();
        Long totalTesters = userRepository.countByRole(User.Role.TESTER);
        Long totalCompanies = userRepository.countByRole(User.Role.COMPANY);
        Long totalAssignments = gameAssignmentRepository.count();
        Long activeAssignments = gameAssignmentRepository.countByStatus("в работе");
        Long completedAssignments = gameAssignmentRepository.countByStatus("завершено");
        Long totalFeedbacks = feedbackRepository.count();
        Long finalFeedbacks = feedbackRepository.countByFeedbackType(Feedback.FeedbackType.FINAL);

        return new AdminDashboardStatsDTO(
            totalGames, totalUsers, totalTesters, totalCompanies,
            totalAssignments, activeAssignments, completedAssignments,
            totalFeedbacks, finalFeedbacks
        );
    }

    public List<AdminGameStatsDTO> getAllGamesWithStats() {
        List<Game> games = gameRepository.findAll();

        return games.stream().map(game -> {
            Long totalAssignments = gameAssignmentRepository.countByGameId(game.getId());
            Long activeAssignments = gameAssignmentRepository.countByGameIdAndStatus(game.getId(), "в работе");
            Long completedAssignments = gameAssignmentRepository.countByGameIdAndStatus(game.getId(), "завершено");
            Long totalFeedbacks = feedbackRepository.countByGameId(game.getId());
            Double averageRating = feedbackRepository.getAverageRatingByGameId(game.getId());

            return new AdminGameStatsDTO(game, totalAssignments, activeAssignments,
                                       completedAssignments, totalFeedbacks, averageRating);
        }).collect(Collectors.toList());
    }

    public List<AdminUserDTO> getAllUsersWithStats() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            Long totalAssignments = 0L;
            Long completedAssignments = 0L;
            Long totalFeedbacks = 0L;

            if (user.getRole() == User.Role.TESTER) {
                totalAssignments = gameAssignmentRepository.countByTesterId(user.getId());
                completedAssignments = gameAssignmentRepository.countByTesterIdAndStatus(user.getId(), "завершено");
                totalFeedbacks = feedbackRepository.countByTesterId(user.getId());
            } else if (user.getRole() == User.Role.COMPANY) {
                totalAssignments = gameAssignmentRepository.countByCompanyId(user.getId());
                totalFeedbacks = feedbackRepository.countByCompanyId(user.getId());
            }

            return new AdminUserDTO(user, totalAssignments, completedAssignments, totalFeedbacks);
        }).collect(Collectors.toList());
    }

    public List<AdminAssignmentDTO> getAllAssignments() {
        List<GameAssignment> assignments = gameAssignmentRepository.findAllWithGameAndTester();

        return assignments.stream().map(assignment -> {
            Long feedbacksCount = feedbackRepository.countByGameAssignment(assignment);
            Boolean hasFinalFeedback = feedbackRepository.existsFinalFeedbackForAssignment(assignment);

            return new AdminAssignmentDTO(assignment, feedbacksCount, hasFinalFeedback);
        }).collect(Collectors.toList());
    }

    public List<FeedbackDTO> getAllFeedbacks() {
        List<Feedback> feedbacks = feedbackRepository.findAllOrderByCreatedAtDesc();
        return feedbacks.stream()
                .map(FeedbackDTO::new)
                .collect(Collectors.toList());
    }

    // Новые методы для расширенного управления пользователями
    public AdminUserDTO blockUser(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Нельзя заблокировать администратора");
        }

        user.setBlocked(true);
        user.setBlockedReason(reason);
        userRepository.save(user);

        // Отменяем все активные назначения заблокированного пользователя
        if (user.getRole() == User.Role.TESTER) {
            List<GameAssignment> activeAssignments = gameAssignmentRepository
                    .findByTesterIdAndStatus(userId, "в работе");

            for (GameAssignment assignment : activeAssignments) {
                assignment.setStatus("отменено");
                gameAssignmentRepository.save(assignment);
            }
        }

        return getUserWithStats(user);
    }

    public AdminUserDTO unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        user.setBlocked(false);
        userRepository.save(user);

        return getUserWithStats(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Нельзя удалить администратора");
        }

        // Проверяем, есть ли активные назначения
        List<GameAssignment> activeAssignments = gameAssignmentRepository
                .findByTesterIdAndStatus(userId, "в работе");

        if (!activeAssignments.isEmpty()) {
            throw new RuntimeException("Нельзя удалить пользователя с активными назначениями");
        }

        userRepository.delete(user);
    }

    // Методы для управления играми
    public AdminGameStatsDTO updateGame(Long gameId, AdminGameEditRequestDTO request) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            game.setTitle(request.getTitle().trim());
        }

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            game.setStatus(request.getStatus().trim());
        }

        gameRepository.save(game);

        return getGameWithStats(game);
    }

    public void deleteGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, есть ли активные назначения
        List<GameAssignment> activeAssignments = gameAssignmentRepository
                .findByGameIdAndStatus(gameId, "в работе");

        if (!activeAssignments.isEmpty()) {
            throw new RuntimeException("Нельзя удалить игру с активными назначениями");
        }

        gameRepository.delete(game);
    }

    // Методы для управления назначениями
    public AdminAssignmentDTO cancelAssignment(Long assignmentId, String reason) {
        GameAssignment assignment = gameAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Назначение не найдено"));

        if (!"в работе".equals(assignment.getStatus())) {
            throw new RuntimeException("Можно отменить только активные назначения");
        }

        assignment.setStatus("отменено - " + reason);
        gameAssignmentRepository.save(assignment);

        Long feedbacksCount = feedbackRepository.countByGameAssignment(assignment);
        Boolean hasFinalFeedback = feedbackRepository.existsFinalFeedbackForAssignment(assignment);

        return new AdminAssignmentDTO(assignment, feedbacksCount, hasFinalFeedback);
    }

    public void deleteAssignment(Long assignmentId) {
        GameAssignment assignment = gameAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Назначение не найдено"));

        if ("в работе".equals(assignment.getStatus())) {
            throw new RuntimeException("Нельзя удалить активное назначение");
        }

        gameAssignmentRepository.delete(assignment);
    }

    // Получение статистики по заблокированным пользователям
    public Long getBlockedUsersCount() {
        return userRepository.countByBlocked(true);
    }

    // Вспомогательные методы
    private AdminUserDTO getUserWithStats(User user) {
        Long totalAssignments = 0L;
        Long completedAssignments = 0L;
        Long totalFeedbacks = 0L;

        if (user.getRole() == User.Role.TESTER) {
            totalAssignments = gameAssignmentRepository.countByTesterId(user.getId());
            completedAssignments = gameAssignmentRepository.countByTesterIdAndStatus(user.getId(), "завершено");
            totalFeedbacks = feedbackRepository.countByTesterId(user.getId());
        } else if (user.getRole() == User.Role.COMPANY) {
            totalAssignments = gameAssignmentRepository.countByCompanyId(user.getId());
            totalFeedbacks = feedbackRepository.countByCompanyId(user.getId());
        }

        return new AdminUserDTO(user, totalAssignments, completedAssignments, totalFeedbacks);
    }

    private AdminGameStatsDTO getGameWithStats(Game game) {
        Long totalAssignments = gameAssignmentRepository.countByGameId(game.getId());
        Long activeAssignments = gameAssignmentRepository.countByGameIdAndStatus(game.getId(), "в работе");
        Long completedAssignments = gameAssignmentRepository.countByGameIdAndStatus(game.getId(), "завершено");
        Long totalFeedbacks = feedbackRepository.countByGameId(game.getId());
        Double averageRating = feedbackRepository.getAverageRatingByGameId(game.getId());

        return new AdminGameStatsDTO(game, totalAssignments, activeAssignments,
                                   completedAssignments, totalFeedbacks, averageRating);
    }
}
