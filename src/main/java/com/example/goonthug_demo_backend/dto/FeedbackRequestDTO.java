package com.example.goonthug_demo_backend.dto;

import com.example.goonthug_demo_backend.model.Feedback;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FeedbackRequestDTO {

    @NotNull(message = "ID игры обязательно")
    private Long gameId;

    private Double rating; // Обязательно только для финального фидбека

    @NotBlank(message = "Комментарий обязателен")
    private String comment;

    @NotNull(message = "Тип фидбека обязателен")
    private Feedback.FeedbackType feedbackType;

    // Конструкторы
    public FeedbackRequestDTO() {}

    public FeedbackRequestDTO(Long gameId, Double rating, String comment, Feedback.FeedbackType feedbackType) {
        this.gameId = gameId;
        this.rating = rating;
        this.comment = comment;
        this.feedbackType = feedbackType;
    }

    // Геттеры и сеттеры
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
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

    public Feedback.FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(Feedback.FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }
}
