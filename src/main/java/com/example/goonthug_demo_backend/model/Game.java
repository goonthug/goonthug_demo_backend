package com.example.goonthug_demo_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "game_demo")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "file_content")
    private byte[] fileContent;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "min_tester_rating")
    private Integer minTesterRating;

    @Column(name = "requires_manual_selection")
    private Boolean requiresManualSelection;

    @Column(name = "status")
    private String status;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private Company company;

    // Геттеры и сеттеры (только необходимые для примера)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public byte[] getFileContent() { return fileContent; }
    public void setFileContent(byte[] fileContent) { this.fileContent = fileContent; }
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
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", fileName='" + fileName + '\'' +
                ", minTesterRating=" + minTesterRating +
                ", requiresManualSelection=" + requiresManualSelection +
                ", status='" + status + '\'' +
                ", companyId=" + (company != null ? company.getId() : null) +
                '}';
    }
}