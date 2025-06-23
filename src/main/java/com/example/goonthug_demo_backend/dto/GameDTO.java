package com.example.goonthug_demo_backend.dto;

public class GameDTO {
    private Long id;
    private String fileName;
    private Integer minTesterRating;
    private Boolean requiresManualSelection;
    private String title;
    private String status;
    private Long companyId; // Только ID компании, если нужно

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Integer getMinTesterRating() { return minTesterRating; }
    public void setMinTesterRating(Integer minTesterRating) { this.minTesterRating = minTesterRating; }
    public Boolean getRequiresManualSelection() { return requiresManualSelection; }
    public void setRequiresManualSelection(Boolean requiresManualSelection) { this.requiresManualSelection = requiresManualSelection; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
}