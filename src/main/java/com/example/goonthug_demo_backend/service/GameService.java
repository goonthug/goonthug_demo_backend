package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.dto.GameDTO;
import com.example.goonthug_demo_backend.model.Company;
import com.example.goonthug_demo_backend.model.Game;
import com.example.goonthug_demo_backend.model.GameAssignment;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.GameAssignmentRepository;
import com.example.goonthug_demo_backend.repository.GameRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameAssignmentRepository gameAssignmentRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    public GameService(GameRepository gameRepository,
                       UserRepository userRepository,
                       GameAssignmentRepository gameAssignmentRepository,
                       UserService userService,
                       ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.gameAssignmentRepository = gameAssignmentRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public Game uploadGame(MultipartFile file, String title, String status) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        Company company = userService.findCompanyByUser(user);

        Game game = new Game();
        game.setTitle(title);
        game.setFileName(file.getOriginalFilename());
        game.setFileContent(file.getBytes());
        game.setStatus(status != null ? status : "доступна");
        game.setCompany(company);

        return gameRepository.save(game);
    }

    public List<GameDTO> getAllGames() {
        List<Game> games = gameRepository.findAllWithCompany();
        return games.stream()
                .map(game -> modelMapper.map(game, GameDTO.class))
                .collect(Collectors.toList());
    }

    public ResponseEntity<Resource> downloadGame(Long gameId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tester not found: " + email));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));

        // Проверяем, что игра доступна
        if (!"доступна".equals(game.getStatus())) {
            throw new RuntimeException("Game is not available for download");
        }

        // Проверяем только то, что конкретный тестер еще не взял эту игру
        boolean alreadyAssignedToThisTester = gameAssignmentRepository
                .existsByGameIdAndTesterIdAndStatus(gameId, tester.getId(), "в работе");

        if (alreadyAssignedToThisTester) {
            throw new RuntimeException("You have already taken this game");
        }

        // Создаем новое назначение для этого тестера
        GameAssignment assignment = new GameAssignment();
        assignment.setGame(game);
        assignment.setTester(tester);
        assignment.setStatus("в работе");
        gameAssignmentRepository.save(assignment);

        // Возвращаем файл
        ByteArrayResource resource = new ByteArrayResource(game.getFileContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + game.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + id));
    }

    public void completeGameTesting(Long gameId, double rating, String feedback) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Tester not found: " + email));

        // Находим активное назначение для этого тестера и игры
        GameAssignment assignment = gameAssignmentRepository
                .findByGameIdAndTesterIdAndStatus(gameId, tester.getId(), "в работе")
                .orElseThrow(() -> new RuntimeException("No active assignment found for this game"));

        // Обновляем статус назначения
        assignment.setStatus("завершено");
        gameAssignmentRepository.save(assignment);

        // Здесь можно добавить логику сохранения рейтинга и отзыва
        // Например, создать отдельную сущность GameReview
    }
}