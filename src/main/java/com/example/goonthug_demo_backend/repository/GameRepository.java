package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g JOIN FETCH g.company")
    List<Game> findAllWithCompany();
}