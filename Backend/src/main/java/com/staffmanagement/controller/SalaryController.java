package com.staffmanagement.controller;

import com.staffmanagement.model.SalaryRequest;
import com.staffmanagement.model.SalaryResponse;
import com.staffmanagement.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/salaries")
@CrossOrigin(origins = "http://localhost:5000")
public class SalaryController {
    
    @Autowired
    private SalaryService salaryService;
    
    @PostMapping("/calculate")
    public ResponseEntity<?> calculateSalary(@RequestBody SalaryRequest request) {
        try {
            SalaryResponse response = salaryService.calculateAndSaveSalary(request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("salary", response);
            result.put("message", "Salary calculated successfully");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getSalary(@PathVariable Long id) {
        try {
            SalaryResponse response = salaryService.getSalary(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("salary", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserSalaries(@PathVariable String username) {
        try {
            List<SalaryResponse> responses = salaryService.getUserSalaries(username);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("salaries", responses);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/user/{username}/month/{yearMonth}")
    public ResponseEntity<?> getSalaryByUserAndMonth(
            @PathVariable String username,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        try {
            SalaryResponse response = salaryService.getSalaryByUserAndMonth(username, yearMonth);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("salary", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/month/{yearMonth}")
    public ResponseEntity<?> getSalariesByMonth(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        try {
            List<SalaryResponse> responses = salaryService.getSalariesByMonth(yearMonth);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("salaries", responses);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateSalaryStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusRequest) {
        try {
            String status = statusRequest.get("status");
            SalaryResponse response = salaryService.updateSalaryStatus(id, status);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("salary", response);
            result.put("message", "Salary status updated to " + status);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/user/{username}/latest")
    public ResponseEntity<?> getLatestSalary(@PathVariable String username) {
        try {
            SalaryResponse response = salaryService.getLatestSalary(username);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("salary", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getSalaryStats() {
        try {
            YearMonth currentMonth = YearMonth.now();
            // You can implement more comprehensive stats here
            Map<String, Object> stats = new HashMap<>();
            stats.put("currentMonth", currentMonth.toString());
            // Add more statistics as needed
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("stats", stats);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}