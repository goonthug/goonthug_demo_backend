package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.dto.UserRegistrationDto;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.UserRepository;
import com.example.goonthug_demo_backend.service.UserService;
import com.example.goonthug_demo_backend.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5175", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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
        try {
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
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        try {
            String username = loginData.get("username");
            String password = loginData.get("password");

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String role = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getAuthorities()
                    .iterator().next().getAuthority().replace("ROLE_", "");
            String token = jwtUtil.generateToken(username, role);
            logger.info("Login successful for user: {}, token generated", username);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/user/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No authenticated user"));
            }

            String username = userDetails.getUsername();
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
        }}}