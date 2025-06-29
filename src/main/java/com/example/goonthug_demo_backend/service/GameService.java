package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.model.Company;
import com.example.goonthug_demo_backend.model.Game;
import com.example.goonthug_demo_backend.model.GameAssignment;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.CompanyRepository;
import com.example.goonthug_demo_backend.repository.GameAssignmentRepository;
import com.example.goonthug_demo_backend.repository.GameRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import com.example.goonthug_demo_backend.dto.GameDTO;
import org.modelmapper.ModelMapper;
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
import java.util.stream.Collectors;

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

    @Autowired
    private ModelMapper modelMapper;

    public Company getCompanyByEmail(String email) {
        System.out.println("Ищем пользователя с email: " + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        System.out.println("Найден пользователь: " + user.getEmail() + ", роль: " + user.getRole());
        if (user.getRole() != User.Role.COMPANY) {
            throw new IllegalArgumentException("Пользователь не является компанией");
        }
        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Компания не найдена для пользователя"));
        System.out.println("Найдена компания с ID: " + company.getId());
        return company;
    }

    public void save(Game game) {
        gameRepository.save(game);
    }

    @Transactional
    public Game uploadGame(MultipartFile file, String title, String email) throws IOException {
        logger.debug("Поиск компании с email: {}", email);
        User companyUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Компания не найдена"));

        if (!companyUser.getRole().equals(User.Role.COMPANY)) {
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
        game.setStatus("available");

        logger.info("Сохранение игры в базу данных: {}", title);
        Game savedGame = gameRepository.save(game);
        logger.info("Игра успешно сохранена с ID: {}", savedGame.getId());
        return savedGame;
    }

    @Transactional
    public void assignGame(Long gameId, String email) {
        logger.debug("Назначение игры с ID {} пользователю с email {}", gameId, email);
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
        logger.debug("Найден тестер: email={}, role={}", tester.getEmail(), tester.getRole());

        if (!User.Role.TESTER.equals(tester.getRole())) {
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
        game.setStatus("в работе");
        gameRepository.save(game);
        logger.info("Игра с ID {} назначена тестеру с email {}", gameId, email);
    }

    @Transactional
    public Game downloadGame(Long gameId, String email) {
        logger.debug("Скачивание игры с ID {} пользователем с email {}", gameId, email);

        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + email));
        logger.debug("Найден тестер: id={}, email={}, role={}", tester.getId(), tester.getEmail(), tester.getRole());

        if (!"TESTER".equals(tester.getRole().name())) {
            throw new IllegalArgumentException("Только тестеры могут скачивать игры. Текущая роль: " + tester.getRole());
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Игра с ID " + gameId + " не найдена"));
        logger.debug("Найдена игра: id={}, status={}", game.getId(), game.getStatus());

        if (game.getFileContent() == null) {
            throw new IllegalArgumentException("Файл для игры с ID " + gameId + " не загружен");
        }

        return game;
    }

    public List<GameDTO> getAllGames() {
        try {
            List<Game> games = gameRepository.findAllWithCompany();
            logger.info("Найдено игр в game_demo: {}", games.size());
            games.forEach(game -> {
                try {
                    logger.debug("Проверка игры ID: {}, текущий статус: {}", game.getId(), game.getStatus());
                    if (gameAssignmentRepository.existsByGameIdAndStatus(game.getId(), "в работе")) {
                        game.setStatus("в работе");
                        logger.debug("Статус игры {} изменён на 'в работе'", game.getId());
                    } else {
                        game.setStatus("available");
                        logger.debug("Статус игры {} установлен как 'available'", game.getId());
                    }
                } catch (Exception e) {
                    logger.warn("Ошибка при обновлении статуса игры ID {}: {}", game.getId(), e.getMessage());
                    game.setStatus("unknown");
                }
            });
            return games.stream()
                    .map(game -> modelMapper.map(game, GameDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Ошибка при получении списка игр: {}", e.getMessage(), e);
            throw e;
        }
    }
}