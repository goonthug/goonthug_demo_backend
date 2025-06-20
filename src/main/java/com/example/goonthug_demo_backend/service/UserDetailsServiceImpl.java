// src/main/java/com/example/goonthug_demo_backend/service/UserDetailsServiceImpl.java
package com.example.goonthug_demo_backend.service;

import com.example.goonthug_demo_backend.model.User;
import com.example.goonthug_demo_backend.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        System.out.println("Found user: " + user.getUsername() + ", role: " + user.getRole()); // Отладка

        // Создаем список authorities с ролью
        String role = "ROLE_" + user.getRole().toString();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority) // Список ролей
        );
    }
}