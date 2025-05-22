package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.model.Game;
import com.example.goonthug_demo_backend.model.GameAssignment;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.GameAssignmentRepository;
import com.example.goonthug_demo_backend.repository.GameRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Autowired
    public GameService(GameAssignmentRepository assignmentRepository,
                       UserRepository userRepository,
                       GameRepository gameRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public void assignGame(Long gameId, String username) {
        User tester = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Тестер не найден"));

        if (!tester.getRole().equals("TESTER")) {
            throw new AccessDeniedException("Только тестеры могут брать игры в работу");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        // Проверяем, не взята ли игра другим тестером
        boolean isAssigned = assignmentRepository.existsByGameIdAndStatus(gameId, "в работе");
        if (isAssigned) {
            throw new RuntimeException("Игра уже взята в работу другим тестером");
        }

        GameAssignment assignment = new GameAssignment();
        assignment.setGame(game);
        assignment.setTester(tester);
        assignment.setStatus("в работе");
        assignmentRepository.save(assignment);
    }
}