package com.example.goonthug_demo_backend.dto;

import com.example.goonthug_demo_backend.model.User;
import java.time.LocalDateTime;

public class AdminUserDTO {
    private Long id;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String companyName;
    private Boolean blocked;
    private LocalDateTime blockedAt;
    private String blockedReason;
    private Long totalAssignments;
    private Long completedAssignments;
    private Long totalFeedbacks;

    public AdminUserDTO(User user, Long totalAssignments, Long completedAssignments, Long totalFeedbacks) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole().toString();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.companyName = user.getCompanyName();
        this.blocked = user.getBlocked();
        this.blockedAt = user.getBlockedAt();
        this.blockedReason = user.getBlockedReason();
        this.totalAssignments = totalAssignments != null ? totalAssignments : 0L;
        this.completedAssignments = completedAssignments != null ? completedAssignments : 0L;
        this.totalFeedbacks = totalFeedbacks != null ? totalFeedbacks : 0L;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }

    public LocalDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }

    public String getBlockedReason() { return blockedReason; }
    public void setBlockedReason(String blockedReason) { this.blockedReason = blockedReason; }

    public Long getTotalAssignments() { return totalAssignments; }
    public void setTotalAssignments(Long totalAssignments) { this.totalAssignments = totalAssignments; }

    public Long getCompletedAssignments() { return completedAssignments; }
    public void setCompletedAssignments(Long completedAssignments) { this.completedAssignments = completedAssignments; }

    public Long getTotalFeedbacks() { return totalFeedbacks; }
    public void setTotalFeedbacks(Long totalFeedbacks) { this.totalFeedbacks = totalFeedbacks; }
}
