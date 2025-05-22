package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.model.GameDemo;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.GameDemoRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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
    private final Path fileStorageLocation;

    public GameDemoService(GameDemoRepository gameDemoRepository,
                           UserRepository userRepository,
                           @Value("${file.upload-dir:uploads}") String uploadDir) {
        this.gameDemoRepository = gameDemoRepository;
        this.userRepository = userRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public GameDemo uploadGameDemo(MultipartFile file, String title, Double minTesterRating, Boolean requiresManualSelection) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User company = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!company.getRole().equals("COMPANY")) {
            throw new RuntimeException("Only users with COMPANY role can upload game demos");
        }

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        GameDemo gameDemo = new GameDemo();
        gameDemo.setTitle(title);
        gameDemo.setFilePath(targetLocation.toString());
        gameDemo.setCompany(company);
        gameDemo.setMinTesterRating(minTesterRating);
        gameDemo.setRequiresManualSelection(requiresManualSelection);

        return gameDemoRepository.save(gameDemo);
    }

    public Resource downloadGameDemo(Long id) throws MalformedURLException {
        GameDemo gameDemo = gameDemoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Game demo not found with id: " + id));

        Path filePath = Paths.get(gameDemo.getFilePath()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found: " + gameDemo.getFilePath());
        }

        return resource;
    }
}