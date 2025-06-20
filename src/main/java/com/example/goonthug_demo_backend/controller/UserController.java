// src/main/java/com/example/goonthug_demo_backend/controller/UserController.java
package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.dto.UserRegistrationDto; // Импорт существующего DTO
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.UserRepository;
import com.example.goonthug_demo_backend.service.UserService;
import com.example.goonthug_demo_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5176", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationData) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(registrationData.get("username"));
        dto.setPassword(registrationData.get("password"));
        dto.setRole(registrationData.get("role"));
        if ("COMPANY".equalsIgnoreCase(dto.getRole())) {
            dto.setCompanyName(registrationData.get("companyName"));
        } else {
            dto.setFirstName(registrationData.get("firstName"));
            dto.setLastName(registrationData.get("lastName"));
        }
        userService.registerUser(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        String role = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getAuthorities()
                .iterator().next().getAuthority().replace("ROLE_", ""); // Извлекаем роль без ROLE_
        String token = jwtUtil.generateToken(username, role);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/user/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        try {
            String username = jwt.getSubject();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("role", user.getRole().toString());

            if (user.getRole() == User.Role.TESTER) {
                response.put("tests", user.getTests());
            } else if (user.getRole() == User.Role.COMPANY) {
                response.put("ratedGames", user.getRatedGames());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка загрузки профиля: " + e.getMessage()));
        }
    }
}