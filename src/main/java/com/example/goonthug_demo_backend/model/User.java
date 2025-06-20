// src/main/java/com/example/goonthug_demo_backend/model/User.java
package com.example.goonthug_demo_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // Хранится в хешированном виде

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private String firstName; // Для тестера
    private String lastName; // Для тестера
    private String companyName; // Для компании

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Test> tests = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RatedGame> ratedGames = new ArrayList<>();

    // Геттеры, сеттеры, конструкторы
    public enum Role {
        TESTER, COMPANY
    }

    // Убери вложенные классы Test и RatedGame, так как они теперь отдельные сущности

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public List<Test> getTests() { return tests; }
    public void setTests(List<Test> tests) { this.tests = tests; }
    public List<RatedGame> getRatedGames() { return ratedGames; }
    public void setRatedGames(List<RatedGame> ratedGames) { this.ratedGames = ratedGames; }
}