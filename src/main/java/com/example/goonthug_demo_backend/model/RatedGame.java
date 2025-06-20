// src/main/java/com/example/goonthug_demo_backend/model/RatedGame.java
package com.example.goonthug_demo_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_rated_games")
public class RatedGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Double averageRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Конструкторы
    public RatedGame() {}

    public RatedGame(Long id, String title, Double averageRating) {
        this.id = id;
        this.title = title;
        this.averageRating = averageRating;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}