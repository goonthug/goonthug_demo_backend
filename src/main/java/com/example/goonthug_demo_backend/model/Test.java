// src/main/java/com/example/goonthug_demo_backend/model/Test.java
package com.example.goonthug_demo_backend.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tests")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gameTitle;
    private Double rating;
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Конструкторы
    public Test() {}

    public Test(Long id, String gameTitle, Double rating, LocalDateTime date) {
        this.id = id;
        this.gameTitle = gameTitle;
        this.rating = rating;
        this.date = date;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getGameTitle() { return gameTitle; }
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}