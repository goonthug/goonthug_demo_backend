package com.example.goonthug_demo_backend.model;

import jakarta.persistence.*;

@Entity
public class GameDemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String filePath;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private User company;

    private Double minTesterRating;

    private Boolean requiresManualSelection;

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public User getCompany() {
        return company;
    }

    public void setCompany(User company) {
        this.company = company;
    }

    public Double getMinTesterRating() {
        return minTesterRating;
    }

    public void setMinTesterRating(Double minTesterRating) {
        this.minTesterRating = minTesterRating;
    }

    public Boolean getRequiresManualSelection() {
        return requiresManualSelection;
    }

    public void setRequiresManualSelection(Boolean requiresManualSelection) {
        this.requiresManualSelection = requiresManualSelection;
    }
}