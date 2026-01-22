package com.staffmanagement.controller;

import com.staffmanagement.model.Leave;
import com.staffmanagement.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaves")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaveController {
    
    @Autowired
    private LeaveService leaveService;
    
    // ✅ EMPLOYEE: Apply for leave
    @PostMapping("/apply")
    public ResponseEntity<?> applyForLeave(@RequestBody Leave leaveRequest) {
        try {
            Leave leave = leaveService.applyForLeave(leaveRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Leave application submitted successfully");
            response.put("leave", leave);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ✅ EMPLOYEE: View own leaves
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserLeaves(@PathVariable String username) {
        try {
            List<Leave> leaves = leaveService.getUserLeaves(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("leaves", leaves);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ✅ EMPLOYEE/MANAGER: View leave balance
    @GetMapping("/balance/{username}")
    public ResponseEntity<?> getLeaveBalance(@PathVariable String username) {
        try {
            LeaveService.LeaveBalance balance = leaveService.getLeaveBalance(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("balance", balance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ✅ MANAGER ONLY: View pending leaves for team
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('ADMIN')")
    @GetMapping("/manager/{managerUsername}/pending")
    public ResponseEntity<?> getPendingLeavesForManager(@PathVariable String managerUsername) {
        try {
            List<Leave> leaves = leaveService.getPendingLeavesForManager(managerUsername);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("leaves", leaves);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ✅ MANAGER ONLY: Approve leave
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{leaveId}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long leaveId, @RequestBody Map<String, String> request) {
        try {
            String approvedBy = request.get("approvedBy");
            String comments = request.get("comments");
            
            Leave leave = leaveService.approveLeave(leaveId, approvedBy, comments);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Leave approved successfully");
            response.put("leave", leave);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ✅ MANAGER ONLY: Reject leave
    @PreAuthorize("hasRole('PROJECT_MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{leaveId}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long leaveId, @RequestBody Map<String, String> request) {
        try {
            String rejectedBy = request.get("rejectedBy");
            String reason = request.get("reason");
            
            Leave leave = leaveService.rejectLeave(leaveId, rejectedBy, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Leave rejected");
            response.put("leave", leave);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // ✅ ADMIN ONLY: View all pending leaves (existing endpoint)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingLeaves() {
        try {
            List<Leave> leaves = leaveService.getPendingLeaves();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("leaves", leaves);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/date-range")
    public ResponseEntity<?> getLeavesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Leave> leaves = leaveService.getLeavesByDateRange(startDate, endDate);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("leaves", leaves);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
