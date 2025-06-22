package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.model.Company;
import com.example.goonthug_demo_backend.model.Game;
import com.example.goonthug_demo_backend.model.GameAssignment;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.CompanyRepository;
import com.example.goonthug_demo_backend.repository.GameAssignmentRepository;
import com.example.goonthug_demo_backend.repository.GameRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameAssignmentRepository gameAssignmentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Transactional
    public Game uploadGame(MultipartFile file, String title, String username) throws IOException {
        logger.debug("Поиск компании с username: {}", username);
        User companyUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Компания не найдена"));

        if (!companyUser.getRole().equals("COMPANY")) {
            throw new IllegalArgumentException("Только компании могут загружать игры");
        }

        Company company = companyRepository.findByUser(companyUser)
                .orElseThrow(() -> new IllegalArgumentException("Запись компании не найдена"));

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        Path targetLocation = Paths.get("uploads").resolve(uniqueFileName);
        Files.createDirectories(targetLocation.getParent());
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Game game = new Game();
        game.setTitle(title);
        game.setFileName(uniqueFileName);
        game.setFileContent(file.getBytes());
        game.setCompany(company);
        game.setStatus("available"); // Устанавливаем начальный статус

        logger.info("Сохранение игры в базу данных: {}", title);
        Game savedGame = gameRepository.save(game);
        logger.info("Игра успешно сохранена с ID: {}", savedGame.getId());
        return savedGame;
    }

    @Transactional
    public void assignGame(Long gameId, String username) {
        logger.debug("Назначение игры с ID {} пользователю {}", gameId, username);
        User tester = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (!tester.getRole().equals("TESTER")) {
            throw new IllegalArgumentException("Только тестеры могут брать игры в работу");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра с ID " + gameId + " не найдена"));

        if (gameAssignmentRepository.existsByGameIdAndStatus(gameId, "в работе")) {
            throw new IllegalArgumentException("Игра уже взята в работу другим тестером");
        }

        GameAssignment assignment = new GameAssignment();
        assignment.setGame(game);
        assignment.setTester(tester);
        assignment.setStatus("в работе");

        gameAssignmentRepository.save(assignment);
        game.setStatus("в работе"); // Обновляем статус игры
        gameRepository.save(game); // Сохраняем обновлённый статус
        logger.info("Игра с ID {} назначена тестеру {}", gameId, username);
    }

    @Transactional
    public Game downloadGame(Long gameId, String username) {
        logger.debug("Скачивание игры с ID {} пользователем {}", gameId, username);

        User tester = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (!tester.getRole().equals("TESTER")) {
            throw new IllegalArgumentException("Только тестеры могут скачивать игры");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра с ID " + gameId + " не найдена"));

        boolean isAssigned = gameAssignmentRepository.existsByGameIdAndTesterIdAndStatus(
                gameId, tester.getId(), "в работе");
        if (!isAssigned) {
            throw new IllegalArgumentException("Игра не назначена этому тестеру или не находится в работе");
        }

        return game;
    }

    public List<Game> getAllGames() {
        List<Game> games = gameRepository.findAll();
        games.forEach(game -> {
            if (gameAssignmentRepository.existsByGameIdAndStatus(game.getId(), "в работе")) {
                game.setStatus("в работе");
            } else {
                game.setStatus("available");
            }
        });
        return games;
    }
}