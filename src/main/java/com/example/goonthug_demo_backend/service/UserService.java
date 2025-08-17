package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.dto.UserRegistrationDto;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        // Проверяем, что email уникален
        if (userRepository.findByEmail(registrationDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email уже используется: " + registrationDto.getEmail());
        }

        // Создаем пользователя
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(User.Role.valueOf(registrationDto.getRole().toUpperCase()));

        // Заполняем дополнительные поля в зависимости от роли
        if (user.getRole() == User.Role.TESTER) {
            user.setFirstName(registrationDto.getFirstName());
            user.setLastName(registrationDto.getLastName());
        } else if (user.getRole() == User.Role.COMPANY) {
            user.setCompanyName(registrationDto.getCompanyName());
        }

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с email: " + email));
    }
}