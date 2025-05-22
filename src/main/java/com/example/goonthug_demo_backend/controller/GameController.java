package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.model.Game;
import com.example.goonthug_demo_backend.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasRole('TESTER')")
    public ResponseEntity<String> assignGameToTester(@PathVariable Long id, Principal principal) {
        logger.info("Тестер {} пытается взять игру с id {}", principal.getName(), id);
        try {
            gameService.assignGame(id, principal.getName());
            logger.info("Игра с id {} успешно взята тестером {}", id, principal.getName());
            return ResponseEntity.ok("Игра успешно взята в работу!");
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка при взятии игры: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Внутренняя ошибка при взятии игры: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> uploadGame(@RequestParam("file") MultipartFile file,
                                        @RequestParam("title") String title,
                                        Principal principal) {
        logger.info("Пользователь {} пытается загрузить игру: {}", principal.getName(), title);
        try {
            Game game = gameService.uploadGame(file, title, principal.getName());
            logger.info("Игра {} успешно загружена пользователем {}", title, principal.getName());
            return ResponseEntity.ok(game);
        } catch (IOException e) {
            logger.error("Ошибка при загрузке файла: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке файла: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка при загрузке игры: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasRole('TESTER')")
    public ResponseEntity<byte[]> downloadGame(@PathVariable Long id, Principal principal) {
        if (id <= 0) {
            logger.warn("Некорректный gameId: {}", id);
            return ResponseEntity.badRequest().body("ID игры должен быть положительным".getBytes());
        }
        logger.info("Тестер {} пытается скачать игру с id {}", principal.getName(), id);
        try {
            Game game = gameService.downloadGame(id, principal.getName());
            logger.info("Игра с id {} успешно скачана тестером {}", id, principal.getName());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=\"" + game.getFileName() + "\"")
                    .body(game.getFileContent());
        } catch (IllegalArgumentException e) {
            logger.warn("Ошибка при скачивании игры: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (Exception e) {
            logger.error("Внутренняя ошибка при скачивании игры: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера: ".getBytes());
        }
    }
}