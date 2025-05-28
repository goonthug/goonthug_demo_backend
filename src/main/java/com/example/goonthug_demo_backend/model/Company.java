package com.example.goonthug_demo_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companies") // Убедитесь, что имя таблицы правильное
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name") // Соответствует ли имени колонки в БД?
    private String companyName;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Конструкторы
    public Company() {
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}