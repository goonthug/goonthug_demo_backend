package com.example.goonthug_demo_backend.dto;

public class AdminDashboardStatsDTO {
    private Long totalGames;
    private Long totalUsers;
    private Long totalTesters;
    private Long totalCompanies;
    private Long totalAssignments;
    private Long activeAssignments;
    private Long completedAssignments;
    private Long totalFeedbacks;
    private Long finalFeedbacks;

    public AdminDashboardStatsDTO() {}

    public AdminDashboardStatsDTO(Long totalGames, Long totalUsers, Long totalTesters, Long totalCompanies,
                                  Long totalAssignments, Long activeAssignments, Long completedAssignments,
                                  Long totalFeedbacks, Long finalFeedbacks) {
        this.totalGames = totalGames;
        this.totalUsers = totalUsers;
        this.totalTesters = totalTesters;
        this.totalCompanies = totalCompanies;
        this.totalAssignments = totalAssignments;
        this.activeAssignments = activeAssignments;
        this.completedAssignments = completedAssignments;
        this.totalFeedbacks = totalFeedbacks;
        this.finalFeedbacks = finalFeedbacks;
    }

    // Getters and setters
    public Long getTotalGames() { return totalGames; }
    public void setTotalGames(Long totalGames) { this.totalGames = totalGames; }

    public Long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(Long totalUsers) { this.totalUsers = totalUsers; }

    public Long getTotalTesters() { return totalTesters; }
    public void setTotalTesters(Long totalTesters) { this.totalTesters = totalTesters; }

    public Long getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(Long totalCompanies) { this.totalCompanies = totalCompanies; }

    public Long getTotalAssignments() { return totalAssignments; }
    public void setTotalAssignments(Long totalAssignments) { this.totalAssignments = totalAssignments; }

    public Long getActiveAssignments() { return activeAssignments; }
    public void setActiveAssignments(Long activeAssignments) { this.activeAssignments = activeAssignments; }

    public Long getCompletedAssignments() { return completedAssignments; }
    public void setCompletedAssignments(Long completedAssignments) { this.completedAssignments = completedAssignments; }

    public Long getTotalFeedbacks() { return totalFeedbacks; }
    public void setTotalFeedbacks(Long totalFeedbacks) { this.totalFeedbacks = totalFeedbacks; }

    public Long getFinalFeedbacks() { return finalFeedbacks; }
    public void setFinalFeedbacks(Long finalFeedbacks) { this.finalFeedbacks = finalFeedbacks; }
}
