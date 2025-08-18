package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.dto.FeedbackDTO;
import com.example.goonthug_demo_backend.dto.FeedbackRequestDTO;
import com.example.goonthug_demo_backend.model.Feedback;
import com.example.goonthug_demo_backend.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feedback")
@Tag(name = "Feedback Controller", description = "API для работы с фидбеками")
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TESTER')")
    @Operation(summary = "Создать фидбек", description = "Создание фидбека во время тестирования")
    public ResponseEntity<FeedbackDTO> createFeedback(@Valid @RequestBody FeedbackRequestDTO request) {
        try {
            FeedbackDTO feedback = feedbackService.createFeedback(request);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            logger.error("Ошибка при создании фидбека", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/complete-test")
    @PreAuthorize("hasRole('TESTER')")
    @Operation(summary = "Завершить тестирование с финальным фидбеком",
               description = "Оставить финальный фидбек и завершить тестирование")
    public ResponseEntity<FeedbackDTO> completeTestWithFeedback(@Valid @RequestBody FeedbackRequestDTO request) {
        try {
            FeedbackDTO feedback = feedbackService.createFinalFeedbackAndCompleteTest(request);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            logger.error("Ошибка при завершении тестирования с финальным фидбеком", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/game/{gameId}")
    @Operation(summary = "Получить все фидбеки для игры",
               description = "Получение всех фидбеков для конкретной игры")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksForGame(@PathVariable Long gameId) {
        try {
            List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksForGame(gameId);
            return ResponseEntity.ok(feedbacks);
        } catch (RuntimeException e) {
            logger.error("Ошибка при получении фидбеков для игры с ID " + gameId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/assignment/{assignmentId}")
    @Operation(summary = "Получить фидбеки для назначения",
               description = "Получение всех фидбеков для конкретного назначения")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksForAssignment(@PathVariable Long assignmentId) {
        try {
            List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksForAssignment(assignmentId);
            return ResponseEntity.ok(feedbacks);
        } catch (RuntimeException e) {
            logger.error("Ошибка при получении фидбеков для назначения с ID " + assignmentId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('TESTER')")
    @Operation(summary = "Получить мои фидбеки",
               description = "Получение всех фидбеков текущего тестера")
    public ResponseEntity<List<FeedbackDTO>> getMyFeedbacks() {
        try {
            List<FeedbackDTO> feedbacks = feedbackService.getMyFeedbacks();
            return ResponseEntity.ok(feedbacks);
        } catch (RuntimeException e) {
            logger.error("Ошибка при получении фидбеков текущего тестера", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/types")
    @Operation(summary = "Получить типы фидбеков",
               description = "Получение всех доступных типов фидбеков")
    public ResponseEntity<List<String>> getFeedbackTypes() {
        List<String> types = Arrays.stream(Feedback.FeedbackType.values())
                .map(Feedback.FeedbackType::getDisplayName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(types);
    }

    @GetMapping("/game/{gameId}/has-final")
    @PreAuthorize("hasRole('TESTER')")
    @Operation(summary = "Проверить наличие финального фидбека для игры",
               description = "Проверка, оставлен ли уже финальный фидбек для игры текущим тестером")
    public ResponseEntity<Boolean> hasFinalFeedbackForGame(@PathVariable Long gameId) {
        try {
            boolean hasFinal = feedbackService.hasFinalFeedbackForGame(gameId);
            return ResponseEntity.ok(hasFinal);
        } catch (RuntimeException e) {
            logger.error("Ошибка при проверке наличия финального фидбека для игры с ID " + gameId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/assignment/{assignmentId}/has-final")
    @Operation(summary = "Проверить наличие финального фидбека для назначения",
               description = "Проверка, оставлен ли уже финальный фидбек для назначения")
    public ResponseEntity<Boolean> hasFinalFeedback(@PathVariable Long assignmentId) {
        try {
            boolean hasFinal = feedbackService.hasFinalFeedback(assignmentId);
            return ResponseEntity.ok(hasFinal);
        } catch (RuntimeException e) {
            logger.error("Ошибка при проверке наличия финального фидбека для назначения с ID " + assignmentId, e);
            return ResponseEntity.badRequest().build();
        }
    }
}
