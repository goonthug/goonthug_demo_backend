package com.example.goonthug_demo_backend.dto;

import com.example.goonthug_demo_backend.model.Game;

public class AdminGameStatsDTO {
    private Long id;
    private String title;
    private String description;
    private String companyName;
    private Long totalAssignments;
    private Long activeAssignments;
    private Long completedAssignments;
    private Long totalFeedbacks;
    private Double averageRating;

    public AdminGameStatsDTO(Game game, Long totalAssignments, Long activeAssignments,
                            Long completedAssignments, Long totalFeedbacks, Double averageRating) {
        this.id = game.getId();
        this.title = game.getTitle();
        this.companyName = game.getCompany().getCompanyName() != null ?
                          game.getCompany().getCompanyName() :
                          game.getCompany().getEmail();
        this.totalAssignments = totalAssignments;
        this.activeAssignments = activeAssignments;
        this.completedAssignments = completedAssignments;
        this.totalFeedbacks = totalFeedbacks;
        this.averageRating = averageRating;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Long getTotalAssignments() { return totalAssignments; }
    public void setTotalAssignments(Long totalAssignments) { this.totalAssignments = totalAssignments; }

    public Long getActiveAssignments() { return activeAssignments; }
    public void setActiveAssignments(Long activeAssignments) { this.activeAssignments = activeAssignments; }

    public Long getCompletedAssignments() { return completedAssignments; }
    public void setCompletedAssignments(Long completedAssignments) { this.completedAssignments = completedAssignments; }

    public Long getTotalFeedbacks() { return totalFeedbacks; }
    public void setTotalFeedbacks(Long totalFeedbacks) { this.totalFeedbacks = totalFeedbacks; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
}
