package com.example.goonthug_demo_backend.repository;



import com.example.goonthug_demo_backend.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}