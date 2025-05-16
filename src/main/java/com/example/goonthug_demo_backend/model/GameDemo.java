package com.example.goonthug_demo_backend.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "game_demos")
@Data
public class GameDemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Название игры

    @Column(nullable = false)
    private String filePath; // Путь к файлу на сервере

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private User company; // Компания-владелец

    @Column
    private Double minTesterRating; // Минимальный рейтинг тестеров (для пункта 6 функционала)

    @Column
    private Boolean requiresManualSelection; // Требуется ли ручной отбор (для пункта 7 функционала)
}