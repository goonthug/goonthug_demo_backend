package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<String> assignGameToTester(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        gameService.assignGame(id, username);
        return ResponseEntity.ok("Игра успешно взята в работу!");
    }
}