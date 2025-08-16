package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.dto.UserRegistrationDto;
import com.example.goonthug_demo_backend.dto.UserLoginDto;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.UserRepository;
import com.example.goonthug_demo_backend.service.UserService;
import com.example.goonthug_demo_backend.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
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
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto registrationData) {
        try {
            userService.registerUser(registrationData);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto loginData) {
        try {
            String email = loginData.getEmail();
            String password = loginData.getPassword();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            String role = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getAuthorities()
                    .iterator().next().getAuthority().replace("ROLE_", "");
            String token = jwtUtil.generateToken(email, role);
            logger.info("Login successful for user: {}, token generated", email);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/user/profile")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(401).body(Map.of("error", "No authenticated user"));
            }

            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            Map<String, Object> response = new HashMap<>();
            response.put("email", user.getEmail());
            response.put("role", user.getRole().toString());

            if (user.getRole() == User.Role.TESTER) {
                response.put("firstName", user.getFirstName());
                response.put("lastName", user.getLastName());
            }

            if (user.getRole() == User.Role.COMPANY) {
                response.put("companyName", user.getCompanyName());
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Ошибка загрузки профиля: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Test endpoint is accessible");
    }
}