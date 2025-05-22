package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.model.GameDemo;
import com.example.goonthug_demo_backend.service.GameDemoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/games")
public class GameDemoController {

    private final GameDemoService gameDemoService;

    public GameDemoController(GameDemoService gameDemoService) {
        this.gameDemoService = gameDemoService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<GameDemo> uploadGameDemo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "minTesterRating", required = false) Double minTesterRating,
            @RequestParam(value = "requiresManualSelection", required = false) Boolean requiresManualSelection) throws Exception {
        GameDemo gameDemo = gameDemoService.uploadGameDemo(file, title, minTesterRating, requiresManualSelection);
        return ResponseEntity.ok(gameDemo);
    }

    @GetMapping("/api/games/demo/download/{id}")
    @PreAuthorize("hasRole('TESTER')")
    public ResponseEntity<?> downloadGameDemo(@PathVariable Long id) throws Exception {
        Resource resource = gameDemoService.downloadGameDemo(id);
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}