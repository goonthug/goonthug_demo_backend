package com.example.goonthug_demo_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_assignment_id", nullable = false)
    private GameAssignment gameAssignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tester_id", nullable = false)
    private User tester;

    @Column(name = "rating")
    private Double rating; // Только для финального фидбека

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_type", nullable = false)
    private FeedbackType feedbackType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum FeedbackType {
        BUG("Баг"),
        SUGGESTION("Предложение"),
        POSITIVE_REVIEW("Положительный отзыв"),
        GENERAL("Общий"),
        FINAL("Финальный");

        private final String displayName;

        FeedbackType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public Feedback() {
        this.createdAt = LocalDateTime.now();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameAssignment getGameAssignment() {
        return gameAssignment;
    }

    public void setGameAssignment(GameAssignment gameAssignment) {
        this.gameAssignment = gameAssignment;
    }

    public User getTester() {
        return tester;
    }

    public void setTester(User tester) {
        this.tester = tester;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", feedbackType=" + feedbackType +
                ", createdAt=" + createdAt +
                '}';
    }
}
