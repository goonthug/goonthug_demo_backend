package com.example.goonthug_demo_backend.repository;



import com.example.goonthug_demo_backend.model.Tester;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TesterRepository extends JpaRepository<Tester, Long> {
}