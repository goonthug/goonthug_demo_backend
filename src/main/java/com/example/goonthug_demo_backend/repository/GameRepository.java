package com.example.goonthug_demo_backend.repository;


import com.example.goonthug_demo_backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}