package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g JOIN FETCH g.company")
    List<Game> findAllWithCompany();

    @Query("SELECT g FROM Game g JOIN FETCH g.company WHERE g.company.id = :companyId")
    List<Game> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT g FROM Game g JOIN FETCH g.company WHERE g.status = 'доступна'")
    List<Game> findAvailableGames();
}