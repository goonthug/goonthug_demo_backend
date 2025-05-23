package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.model.Game;
import com.example.goonthug_demo_backend.model.GameAssignment;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.model.Company;
import com.example.goonthug_demo_backend.repository.GameAssignmentRepository;
import com.example.goonthug_demo_backend.repository.GameRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import com.example.goonthug_demo_backend.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private final GameAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public GameService(GameAssignmentRepository assignmentRepository,
                       UserRepository userRepository,
                       GameRepository gameRepository,
                       CompanyRepository companyRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.companyRepository = companyRepository;
    }

    public void assignGame(Long gameId, String username) {
        logger.debug("Поиск тестера с username: {}", username);
        User tester = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Тестер не найден"));

        if (!tester.getRole().equals("TESTER")) {
            logger.warn("Пользователь {} не является тестером", username);
            throw new AccessDeniedException("Только тестеры могут брать игры в работу");
        }

        logger.debug("Поиск игры с id: {}", gameId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));

        logger.debug("Проверка, взята ли игра с id {} в работу", gameId);
        boolean isAssigned = assignmentRepository.existsByGameIdAndStatus(gameId, "в работе");
        if (isAssigned) {
            logger.warn("Игра с id {} уже взята в работу", gameId);
            throw new IllegalArgumentException("Игра уже взята в работу другим тестером");
        }

        logger.info("Создание назначения игры {} для тестера {}", gameId, username);
        GameAssignment assignment = new GameAssignment();
        assignment.setGame(game);
        assignment.setTester(tester);
        assignment.setStatus("в работе");
        assignmentRepository.save(assignment);
        logger.info("Игра {} успешно назначена тестеру {}", gameId, username);
    }

    public Game uploadGame(MultipartFile file, String title, String username) throws IOException {
        logger.debug("Поиск компании с username: {}", username);
        User company = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Компания не найдена"));

        if (!company.getRole().equals("COMPANY")) {
            logger.warn("Пользователь {} не является компанией", username);
            throw new IllegalArgumentException("Только компании могут загружать игры");
        }

        logger.debug("Сохранение файла игры: {}", title);
        Game game = new Game();
        game.setTitle(title);
        game.setFileName(file.getOriginalFilename());
        game.setFileContent(file.getBytes());
        game.setCompany(companyRepository.findByUser(company)
                .orElseThrow(() -> new IllegalArgumentException("Запись компании не найдена")));

        logger.info("Сохранение игры {} в базе данных", title);
        Game savedGame = gameRepository.save(game);
        logger.info("Игра с id {} успешно сохранена", savedGame.getId());
        return savedGame;
    }

    public Game downloadGame(Long gameId, String username) {
        logger.debug("Поиск тестера с username: {}", username);
        User tester = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Тестер не найден"));

        logger.debug("Поиск игры с id: {}", gameId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));

        logger.debug("Проверка, взята ли игра с id {} тестером {}", gameId, username);
        boolean isAssigned = assignmentRepository.existsByGameIdAndTesterIdAndStatus(gameId, tester.getId(), "в работе");
        if (!isAssigned) {
            logger.warn("Игра с id {} не взята в работу тестером {}", gameId, username);
            throw new IllegalArgumentException("Игра не взята вами в работу");
        }

        if (game.getFileContent() == null || game.getFileContent().length == 0) {
            logger.error("Файл игры с id {} пустой или отсутствует", gameId);
            throw new IllegalArgumentException("Файл игры пустой или отсутствует");
        }

        logger.info("Игра с id {} готова для скачивания тестером {}", gameId, username);
        return game;
    }
}