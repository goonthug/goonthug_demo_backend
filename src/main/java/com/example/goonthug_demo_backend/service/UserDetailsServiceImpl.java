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
        System.out.println("Found user: " + user.getUsername() + ", role: " + user.getRole() + ", role name: " + user.getRole().name()); // Расширенная отладка

        // Преобразование роли в формат ROLE_*
        String role = "ROLE_" + user.getRole().name(); // Используем name() для enum
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        System.out.println("Assigned authority: " + authority.getAuthority()); // Отладка
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(authority) // Список ролей
        );
    }
}