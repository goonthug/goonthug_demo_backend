package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.dto.GameDTO;
import com.example.goonthug_demo_backend.model.Game;
import com.example.goonthug_demo_backend.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "http://localhost:5175", allowedHeaders = "*")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('COMPANY')")
    @Operation(summary = "Загрузить игру", description = "Загружает новую игру (только для компаний)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> uploadGame(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "status", required = false, defaultValue = "доступна") String status) {
        try {
            logger.info("Загрузка игры: {}", title);
            Game game = gameService.uploadGame(file, title, status);
            logger.info("Игра успешно загружена с id: {}", game.getId());
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            logger.error("Ошибка при загрузке игры: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при загрузке игры: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Получить список игр", description = "Возвращает список всех игр")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список игр получен успешно"),
            @ApiResponse(responseCode = "401", description = "Не авторизован")
    })
    public ResponseEntity<List<GameDTO>> getAllGames() {
        try {
            logger.info("Получение всех игр");
            List<GameDTO> games = gameService.getAllGames();
            logger.info("Найдено игр: {}", games.size());
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            logger.error("Ошибка при получении игр: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasRole('TESTER')")
    @Operation(summary = "Скачать игру", description = "Скачивает игру и создает назначение (только для тестеров)")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> downloadGame(@PathVariable Long id) {
        try {
            logger.info("Тестер пытается скачать игру с id: {}", id);
            ResponseEntity<Resource> response = gameService.downloadGame(id);
            logger.info("Игра успешно скачана для id: {}", id);
            return response;
        } catch (RuntimeException e) {
            logger.error("Ошибка при скачивании игры {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка при скачивании игры: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при скачивании игры {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить игру по ID", description = "Возвращает информацию об игре по ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> getGameById(@PathVariable Long id) {
        try {
            logger.info("Получение игры с id: {}", id);
            Game game = gameService.getGameById(id);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            logger.error("Игра не найдена с id {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Ошибка при получении игры {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при получении игры: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('TESTER')")
    @Operation(summary = "Завершить тестирование", description = "Завершает тестирование игры и оставляет оценку")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> completeGameTesting(
            @PathVariable Long id,
            @RequestParam double rating,
            @RequestParam(required = false) String feedback) {
        try {
            logger.info("Завершение тестирования игры для id: {} с оценкой: {}", id, rating);
            gameService.completeGameTesting(id, rating, feedback);
            return ResponseEntity.ok("Тестирование завершено успешно");
        } catch (RuntimeException e) {
            logger.error("Ошибка при завершении тестирования игры {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Ошибка при завершении тестирования: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при завершении тестирования игры {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}