package com.example.goonthug_demo_backend.dto;

import jakarta.validation.constraints.*;

public class UserRegistrationDto {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(min = 5, max = 255, message = "Email должен содержать от 5 до 255 символов")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100, message = "Пароль должен содержать от 6 до 100 символов")
    private String password;

    @NotBlank(message = "Роль обязательна")
    private String role;

    @Size(min = 2, max = 100, message = "Название компании должно содержать от 2 до 100 символов")
    private String companyName;

    @Size(min = 1, max = 50, message = "Имя должно содержать от 1 до 50 символов")
    private String firstName;

    @Size(min = 1, max = 50, message = "Фамилия должна содержать от 1 до 50 символов")
    private String lastName;

    // Геттеры и сеттеры
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}