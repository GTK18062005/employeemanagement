package com.staffmanagement.controller;

import com.staffmanagement.repository.AttendanceRepository;
import com.staffmanagement.repository.SalaryRepository;
import com.staffmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @PostMapping("/reset-database")
    public ResponseEntity<?> resetDatabase() {
        try {
            // Clear data in correct order
            attendanceRepository.deleteAll();
            salaryRepository.deleteAll();
            userRepository.deleteAll();
            
            // Reset sequences
            userRepository.resetUserSequence();
            attendanceRepository.resetAttendanceSequence();
            salaryRepository.resetSalarySequence();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Database reset successfully. Please restart the application.");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error resetting database: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/database-status")
    public ResponseEntity<?> getDatabaseStatus() {
        try {
            long userCount = userRepository.count();
            long attendanceCount = attendanceRepository.count();
            long salaryCount = salaryRepository.count();
            
            Map<String, Object> status = new HashMap<>();
            status.put("users", userCount);
            status.put("attendance", attendanceCount);
            status.put("salaries", salaryCount);
            status.put("database", "H2");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("status", status);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error getting database status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}