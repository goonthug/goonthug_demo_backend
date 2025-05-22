package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.Company;
import com.example.goonthug_demo_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUser(User user);
}