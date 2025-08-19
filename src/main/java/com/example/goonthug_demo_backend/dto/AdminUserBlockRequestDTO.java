package com.example.goonthug_demo_backend.dto;

public class AdminUserBlockRequestDTO {
    private String reason;

    public AdminUserBlockRequestDTO() {}

    public AdminUserBlockRequestDTO(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
