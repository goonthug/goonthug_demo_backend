package com.example.goonthug_demo_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_assignments")
public class GameAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tester_id", nullable = false)
    private User tester;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    // Конструкторы
    public GameAssignment() {
        this.status = "в работе"; // Значение по умолчанию
        this.assignedAt = LocalDateTime.now(); // Текущее время по умолчанию
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getTester() {
        return tester;
    }

    public void setTester(User tester) {
        this.tester = tester;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    // toString для отладки
    @Override
    public String toString() {
        return "GameAssignment{" +
                "id=" + id +
                ", gameId=" + (game != null ? game.getId() : null) +
                ", testerId=" + (tester != null ? tester.getId() : null) +
                ", status='" + status + '\'' +
                ", assignedAt=" + assignedAt +
                '}';
    }
}