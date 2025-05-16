package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.GameDemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameDemoRepository extends JpaRepository<GameDemo, Long> {
}