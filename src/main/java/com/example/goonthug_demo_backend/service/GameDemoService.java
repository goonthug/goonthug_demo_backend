package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.model.GameDemo;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.GameDemoRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
@Service
public class GameDemoService {
    private final GameDemoRepository gameDemoRepository;
    private final UserRepository userRepository;

    public GameDemoService(GameDemoRepository gameDemoRepository,
                           UserRepository userRepository) {
        this.gameDemoRepository = gameDemoRepository;
        this.userRepository = userRepository;
    }

    public GameDemo uploadGameDemo(MultipartFile file, String title,
                                   Double minTesterRating, Boolean requiresManualSelection) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User company = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        GameDemo gameDemo = new GameDemo();
        gameDemo.setTitle(title);
        gameDemo.setFileName(file.getOriginalFilename()); // Сохраняем имя файла
        gameDemo.setFileContent(file.getBytes()); // Сохраняем содержимое файла
        gameDemo.setCompany(company);
        gameDemo.setMinTesterRating(minTesterRating);
        gameDemo.setRequiresManualSelection(requiresManualSelection);

        return gameDemoRepository.save(gameDemo);
    }

    public ResponseEntity<byte[]> downloadGameDemo(Long id) {
        GameDemo gameDemo = gameDemoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game demo not found with id: " + id));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + gameDemo.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(gameDemo.getFileContent());
    }
}