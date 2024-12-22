package com.rohanth.diary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rohanth.diary.model.User;

@Service
public class UserService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User authenticate(String username, String password) {
        System.out.println("Generated hash for 'password': " + passwordEncoder.encode("password"));
        
        String sql = "SELECT * FROM users WHERE username = ?";
        
        // Add debug logging
        System.out.println("Attempting login with username: " + username);
        
        var users = jdbcTemplate.query(sql, (rs, rowNum) -> {
            // Add debug logging
            System.out.println("Found user in database: " + rs.getString("username"));
            System.out.println("Stored hash: " + rs.getString("password"));
            return new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("password")
            );
        }, username);

        if (users.isEmpty()) {
            System.out.println("No user found with username: " + username);
            return null;
        }

        var user = users.get(0);
        boolean matches = passwordEncoder.matches(password, user.getPassword());
        System.out.println("Password match result: " + matches);
        
        return matches ? user : null;
    }

    public boolean createUser(String username, String password) {
        // Check if username already exists
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, username);
        
        if (count > 0) {
            return false; // Username already exists
        }

        // Hash the password and create new user
        String hashedPassword = passwordEncoder.encode(password);
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        jdbcTemplate.update(sql, username, hashedPassword);
        return true;
    }
}
