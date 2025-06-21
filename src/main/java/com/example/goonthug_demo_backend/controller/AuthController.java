package com.example.goonthug_demo_backend.controller;

import com.example.goonthug_demo_backend.dto.UserLoginDto;
import com.example.goonthug_demo_backend.dto.UserRegistrationDto;
import com.example.goonthug_demo_backend.service.UserService;
import com.example.goonthug_demo_backend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5183", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody UserRegistrationDto dto) {
//        try {
//            // Регистрируем пользователя
//            userService.registerUser(dto);
//            // Возвращаем сообщение об успешной регистрации
//            return ResponseEntity.ok("User registered successfully");
//        } catch (IllegalArgumentException e) {
//            // Возвращаем ошибку, если username уже существует
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserLoginDto dto) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
//            String jwt = jwtUtil.generateToken(dto.getUsername(), role);
//            return ResponseEntity.ok(new AuthResponse(jwt));
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Invalid username or password");
//        }
//    }

    // Вспомогательный класс для ответа с токеном
    private static class AuthResponse {
        private String token;

        public AuthResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
        @GetMapping("/test")
        public ResponseEntity<String> test() {
            return ResponseEntity.ok("Test endpoint is accessible");
        }

    }
}