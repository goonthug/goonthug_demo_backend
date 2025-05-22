package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.dto.UserRegistrationDto;
import com.example.goonthug_demo_backend.model.Company;
import com.example.goonthug_demo_backend.model.Tester;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.CompanyRepository;
import com.example.goonthug_demo_backend.repository.TesterRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final TesterRepository testerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       TesterRepository testerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.testerRepository = testerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(UserRegistrationDto dto) {
        // Проверяем, существует ли пользователь с таким username
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + dto.getUsername() + "' already exists");
        }

        // Создаем нового пользователя
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole()); // Используем строку из DTO напрямую
        userRepository.save(user);

        // Создаем запись в зависимости от роли
        if (dto.getRole().equals("COMPANY")) {
            Company company = new Company();
            company.setUser(user);
            company.setCompanyName(dto.getCompanyName());
            companyRepository.save(company);
        } else if (dto.getRole().equals("TESTER")) {
            Tester tester = new Tester();
            tester.setUser(user);
            tester.setFirstName(dto.getFirstName());
            tester.setLastName(dto.getLastName());
            testerRepository.save(tester);
        }
    }
}