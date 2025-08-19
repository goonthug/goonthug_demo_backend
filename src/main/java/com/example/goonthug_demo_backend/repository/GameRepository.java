package com.example.goonthug_demo_backend.repository;

import com.example.goonthug_demo_backend.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    // Для админ панели - загружаем все игры с компаниями
    @Query("SELECT g FROM Game g JOIN FETCH g.company ORDER BY g.id DESC")
    List<Game> findAllWithCompany();

    // Для тестеров - доступные игры с компаниями
    @Query("SELECT g FROM Game g JOIN FETCH g.company WHERE g.status = 'доступна' ORDER BY g.id DESC")
    List<Game> findAvailableGames();

    // Для компаний - их собственные игры
    @Query("SELECT g FROM Game g WHERE g.company.id = :companyId ORDER BY g.id DESC")
    List<Game> findByCompanyId(Long companyId);
}