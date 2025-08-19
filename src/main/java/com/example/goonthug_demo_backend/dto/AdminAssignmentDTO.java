package com.example.goonthug_demo_backend.dto;

import com.example.goonthug_demo_backend.model.GameAssignment;
import java.time.LocalDateTime;

public class AdminAssignmentDTO {
    private Long id;
    private Long gameId;
    private String gameTitle;
    private String companyName;
    private Long testerId;
    private String testerUsername;
    private String testerEmail;
    private String status;
    private LocalDateTime assignedAt;
    private Long feedbacksCount;
    private Boolean hasFinalFeedback;

    public AdminAssignmentDTO(GameAssignment assignment, Long feedbacksCount, Boolean hasFinalFeedback) {
        this.id = assignment.getId();
        this.gameId = assignment.getGame().getId();
        this.gameTitle = assignment.getGame().getTitle();
        this.companyName = assignment.getGame().getCompany().getCompanyName() != null ?
                          assignment.getGame().getCompany().getCompanyName() :
                          assignment.getGame().getCompany().getEmail();
        this.testerId = assignment.getTester().getId();
        this.testerUsername = assignment.getTester().getFirstName() + " " + assignment.getTester().getLastName();
        this.testerEmail = assignment.getTester().getEmail();
        this.status = assignment.getStatus();
        this.assignedAt = assignment.getAssignedAt();
        this.feedbacksCount = feedbacksCount != null ? feedbacksCount : 0L;
        this.hasFinalFeedback = hasFinalFeedback != null ? hasFinalFeedback : false;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public String getGameTitle() { return gameTitle; }
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Long getTesterId() { return testerId; }
    public void setTesterId(Long testerId) { this.testerId = testerId; }

    public String getTesterUsername() { return testerUsername; }
    public void setTesterUsername(String testerUsername) { this.testerUsername = testerUsername; }

    public String getTesterEmail() { return testerEmail; }
    public void setTesterEmail(String testerEmail) { this.testerEmail = testerEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public Long getFeedbacksCount() { return feedbacksCount; }
    public void setFeedbacksCount(Long feedbacksCount) { this.feedbacksCount = feedbacksCount; }

    public Boolean getHasFinalFeedback() { return hasFinalFeedback; }
    public void setHasFinalFeedback(Boolean hasFinalFeedback) { this.hasFinalFeedback = hasFinalFeedback; }
}
