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
        user.setRole(dto.getRole());
        user = userRepository.save(user); // Сохраняем пользователя сразу

        // Создаем связанные сущности
        if ("COMPANY".equals(dto.getRole())) {
            Company company = new Company();
            company.setCompanyName(dto.getCompanyName());
            company.setUser(user);
            companyRepository.save(company);
        } else if ("TESTER".equals(dto.getRole())) {
            Tester tester = new Tester();
            tester.setUser(user);
            tester.setFirstName(dto.getFirstName());
            tester.setLastName(dto.getLastName());
            testerRepository.save(tester);
        }
    }}