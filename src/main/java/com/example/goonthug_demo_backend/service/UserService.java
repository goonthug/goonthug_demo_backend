package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.dto.UserRegistrationDto;
import com.example.goonthug_demo_backend.model.Company;
import com.example.goonthug_demo_backend.model.Tester;
import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.CompanyRepository;
import com.example.goonthug_demo_backend.repository.TesterRepository;
import com.example.goonthug_demo_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final TesterRepository testerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       TesterRepository testerRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.testerRepository = testerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(UserRegistrationDto dto) {
        System.out.println("Starting registration for: " + dto.getUsername());
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.valueOf(dto.getRole().toUpperCase()));
        user = userRepository.save(user); // Сохраняем пользователя сразу

        // Дополнительная валидация ролей
        if ("COMPANY".equalsIgnoreCase(dto.getRole())) {
            if (dto.getCompanyName() == null || dto.getCompanyName().trim().isEmpty()) {
                throw new IllegalArgumentException("Название компании обязательно для роли COMPANY");
            }
            Company company = new Company();
            company.setCompanyName(dto.getCompanyName());
            company.setUser(user);
            companyRepository.save(company);
        } else if ("TESTER".equalsIgnoreCase(dto.getRole())) {
            if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
                throw new IllegalArgumentException("Имя обязательно для роли TESTER");
            }
            if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
                throw new IllegalArgumentException("Фамилия обязательна для роли TESTER");
            }
            Tester tester = new Tester();
            tester.setUser(user);
            tester.setFirstName(dto.getFirstName());
            tester.setLastName(dto.getLastName());
            testerRepository.save(tester);
        }
    }
}