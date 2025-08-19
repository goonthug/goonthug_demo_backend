package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.dto.*;
import com.example.goonthug_demo_backend.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Админ панель")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Получить статистику для дашборда",
               description = "Общая статистика системы для главной страницы админки")
    public ResponseEntity<AdminDashboardStatsDTO> getDashboardStats() {
        AdminDashboardStatsDTO stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/games")
    @Operation(summary = "Получить все игры со статистикой",
               description = "Список всех игр с детальной статистикой по назначениям и фидбекам")
    public ResponseEntity<List<AdminGameStatsDTO>> getAllGamesWithStats() {
        List<AdminGameStatsDTO> games = adminService.getAllGamesWithStats();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/users")
    @Operation(summary = "Получить всех пользователей со статистикой",
               description = "Список всех пользователей с информацией об их активности")
    public ResponseEntity<List<AdminUserDTO>> getAllUsersWithStats() {
        List<AdminUserDTO> users = adminService.getAllUsersWithStats();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/assignments")
    @Operation(summary = "Получить все назначения тестирования",
               description = "Список всех назначений игр тестерам с детальной информацией")
    public ResponseEntity<List<AdminAssignmentDTO>> getAllAssignments() {
        List<AdminAssignmentDTO> assignments = adminService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/feedbacks")
    @Operation(summary = "Получить все фидбеки",
               description = "Список всех фидбеков в системе")
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        List<FeedbackDTO> feedbacks = adminService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    // Управление пользователями
    @PostMapping("/users/{userId}/block")
    @Operation(summary = "Заблокировать пользователя",
               description = "Блокирует пользователя с указанием причины")
    public ResponseEntity<AdminUserDTO> blockUser(@PathVariable Long userId,
                                                  @RequestBody AdminUserBlockRequestDTO request) {
        try {
            AdminUserDTO user = adminService.blockUser(userId, request.getReason());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/users/{userId}/unblock")
    @Operation(summary = "Разблокировать пользователя",
               description = "Снимает блокировку с пользователя")
    public ResponseEntity<AdminUserDTO> unblockUser(@PathVariable Long userId) {
        try {
            AdminUserDTO user = adminService.unblockUser(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Удалить пользователя",
               description = "Полностью удаляет пользователя из системы")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            adminService.deleteUser(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/users/blocked")
    @Operation(summary = "Получить заблокированных пользователей",
               description = "Список всех заблокированных пользователей")
    public ResponseEntity<List<AdminUserDTO>> getBlockedUsers() {
        List<AdminUserDTO> users = adminService.getAllUsersWithStats().stream()
                .filter(user -> user.getBlocked() != null && user.getBlocked())
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Управление играми
    @PutMapping("/games/{gameId}")
    @Operation(summary = "Редактировать игру",
               description = "Обновляет информацию об игре")
    public ResponseEntity<AdminGameStatsDTO> updateGame(@PathVariable Long gameId,
                                                        @RequestBody AdminGameEditRequestDTO request) {
        try {
            AdminGameStatsDTO game = adminService.updateGame(gameId, request);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/games/{gameId}")
    @Operation(summary = "Удалить игру",
               description = "Полностью удаляет игру из системы")
    public ResponseEntity<Void> deleteGame(@PathVariable Long gameId) {
        try {
            adminService.deleteGame(gameId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Управление назначениями
    @PostMapping("/assignments/{assignmentId}/cancel")
    @Operation(summary = "Отменить назначение",
               description = "Отменяет активное назначение тестирования")
    public ResponseEntity<AdminAssignmentDTO> cancelAssignment(@PathVariable Long assignmentId,
                                                              @RequestBody AdminUserBlockRequestDTO request) {
        try {
            AdminAssignmentDTO assignment = adminService.cancelAssignment(assignmentId, request.getReason());
            return ResponseEntity.ok(assignment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/assignments/{assignmentId}")
    @Operation(summary = "Удалить назначение",
               description = "Полностью удаляет назначение из системы")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long assignmentId) {
        try {
            adminService.deleteAssignment(assignmentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/assignments/active")
    @Operation(summary = "Получить активные назначения",
               description = "Список всех активных назначений тестирования")
    public ResponseEntity<List<AdminAssignmentDTO>> getActiveAssignments() {
        List<AdminAssignmentDTO> assignments = adminService.getAllAssignments().stream()
                .filter(assignment -> "в работе".equals(assignment.getStatus()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(assignments);
    }

    // Дополнительная статистика
    @GetMapping("/stats/blocked-users")
    @Operation(summary = "Количество заблокированных пользователей",
               description = "Возвращает количество заблокированных пользователей")
    public ResponseEntity<Long> getBlockedUsersCount() {
        Long count = adminService.getBlockedUsersCount();
        return ResponseEntity.ok(count);
    }
}
