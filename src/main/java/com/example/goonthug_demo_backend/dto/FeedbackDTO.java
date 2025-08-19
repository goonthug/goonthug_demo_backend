package com.example.goonthug_demo_backend.dto;

import com.example.goonthug_demo_backend.model.Feedback;

public class FeedbackDTO {
    private Long id;
    private Long gameId;
    private String gameTitle;
    private String testerEmail;
    private Double rating;
    private String comment;
    private Feedback.FeedbackType feedbackType;
    private String feedbackTypeDisplayName;
    private String createdAt;

    // Конструкторы
    public FeedbackDTO() {}

    public FeedbackDTO(Feedback feedback) {
        this.id = feedback.getId();
        this.gameId = feedback.getGameAssignment().getGame().getId();
        this.gameTitle = feedback.getGameAssignment().getGame().getTitle();
        this.testerEmail = feedback.getTester().getEmail();
        this.rating = feedback.getRating();
        this.comment = feedback.getComment();
        this.feedbackType = feedback.getFeedbackType();
        this.feedbackTypeDisplayName = feedback.getFeedbackType().getDisplayName();
        this.createdAt = feedback.getCreatedAt().toString();
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getTesterEmail() {
        return testerEmail;
    }

    public void setTesterEmail(String testerEmail) {
        this.testerEmail = testerEmail;
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

    public String getFeedbackTypeDisplayName() {
        return feedbackTypeDisplayName;
    }

    public void setFeedbackTypeDisplayName(String feedbackTypeDisplayName) {
        this.feedbackTypeDisplayName = feedbackTypeDisplayName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
