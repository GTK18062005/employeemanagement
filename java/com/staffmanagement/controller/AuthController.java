package com.staffmanagement.controller;

import com.staffmanagement.model.User;
import com.staffmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            Optional<User> user = userRepository.findByUsernameAndPassword(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if (user.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("user", user.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid username or password");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Username already exists");
                return ResponseEntity.ok(response);
            }
            
            // Set default role if not provided
            if (user.getRole() == null) {
                user.setRole("STAFF");
            }
            
            User savedUser = userRepository.save(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Check username availability
    @GetMapping("/check-username/{username}")
    public ResponseEntity<?> checkUsername(@PathVariable String username) {
        boolean exists = userRepository.existsByUsername(username);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}