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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameAssignmentRepository gameAssignmentRepository;

    @Transactional
    public void assignGame(Long gameId, String username) {
        logger.debug("Назначение игры с ID {} пользователю {}", gameId, username);

        // Находим пользователя (тестера)
        User tester = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        // Проверяем, что пользователь имеет роль TESTER
        if (!tester.getRole().equals("TESTER")) {
            throw new IllegalArgumentException("Только тестеры могут брать игры в работу");
        }

        // Находим игру
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра с ID " + gameId + " не найдена"));

        // Проверяем, не назначена ли игра уже другому тестеру
        if (gameAssignmentRepository.existsByGameIdAndStatus(gameId, "в работе")) {
            throw new IllegalArgumentException("Игра уже взята в работу другим тестером");
        }

        // Создаем назначение игры
        GameAssignment assignment = new GameAssignment();
        assignment.setGame(game);
        assignment.setTester(tester);
        assignment.setStatus("в работе");

        gameAssignmentRepository.save(assignment);
        logger.info("Игра с ID {} назначена тестеру {}", gameId, username);
    }
    @Transactional
    public Game uploadGame(MultipartFile file, String title, String username) throws IOException {
        logger.debug("Поиск компании с username: {}", username);
        User companyUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Компания не найдена"));

        if (!companyUser.getRole().equals("COMPANY")) {
            throw new RuntimeException("Только компании могут загружать игры");

        }

        Company company = companyRepository.findByUser(companyUser)
                .orElseThrow(() -> new RuntimeException("Запись компании не найдена"));

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        Path targetLocation = Paths.get("uploads").resolve(uniqueFileName);
        Files.createDirectories(targetLocation.getParent()); // Убедимся, что папка uploads существует
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        Game game = new Game();
        game.setTitle(title);
        game.setFileName(uniqueFileName); // Сохраняем имя файла
        game.setFileContent(file.getBytes()); // Сохраняем содержимое файла как byte[]
        game.setCompany(company);

        logger.info("Сохранение игры в базу данных: {}", title);
        Game savedGame = gameRepository.save(game);
        logger.info("Игра успешно сохранена с ID: {}", savedGame.getId());
        return savedGame;
    }
}