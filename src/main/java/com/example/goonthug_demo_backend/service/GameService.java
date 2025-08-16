package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.dto.GameDTO;
import com.example.goonthug_demo_backend.model.Company;
import com.example.goonthug_demo_backend.model.Game;
import com.example.goonthug_demo_backend.model.GameAssignment;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;
    private final GameAssignmentRepository gameAssignmentRepository;
    private final ModelMapper modelMapper;

    public GameService(GameRepository gameRepository,
                       UserRepository userRepository,
                       CompanyRepository companyRepository,
                       GameAssignmentRepository gameAssignmentRepository,
                       ModelMapper modelMapper) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.gameAssignmentRepository = gameAssignmentRepository;
        this.modelMapper = modelMapper;
    }

    public Game uploadGame(MultipartFile file, String title, String status) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + email));

        Company company = companyRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Не найдена компания для пользователя: " + email));

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
                .orElseThrow(() -> new RuntimeException("Тестер не найден: " + email));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Не найдена игра с ID: " + gameId));

        if (!"доступна".equals(game.getStatus())) {
            throw new RuntimeException("Демо недоступно для скачивания");
        }

        boolean alreadyAssignedToThisTester = gameAssignmentRepository
                .existsByGameIdAndTesterIdAndStatus(gameId, tester.getId(), "в работе");

        if (alreadyAssignedToThisTester) {
            throw new RuntimeException("Вы уже взяли эту игру для тестирования");
        }

        GameAssignment assignment = new GameAssignment();
        assignment.setGame(game);
        assignment.setTester(tester);
        assignment.setStatus("в работе");
        gameAssignmentRepository.save(assignment);

        ByteArrayResource resource = new ByteArrayResource(game.getFileContent());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + game.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    public Game getGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Не найдено демо с ID: " + id));
    }

    public void completeGameTesting(Long gameId, double rating, String feedback) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User tester = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Тестер не найден: " + email));

        GameAssignment assignment = gameAssignmentRepository
                .findByGameIdAndTesterIdAndStatus(gameId, tester.getId(), "в работе")
                .orElseThrow(() -> new RuntimeException("Не найдено активное назначение для игры с ID: " + gameId));

        assignment.setStatus("завершено");
        gameAssignmentRepository.save(assignment);
    }
}