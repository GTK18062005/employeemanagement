package com.staffmanagement.controller;

import com.staffmanagement.model.AttendanceRequest;
import com.staffmanagement.model.AttendanceResponse;
import com.staffmanagement.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:3000")
public class AttendanceController {
    
    @Autowired
    private AttendanceService attendanceService;
    
    // Mark check-in
    @PostMapping("/checkin/{username}")
    public ResponseEntity<?> markCheckIn(@PathVariable String username) {
        try {
            AttendanceResponse response = attendanceService.markCheckIn(username);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Check-in recorded successfully at " + response.getCheckInTime());
            result.put("attendance", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    // Mark check-out
    @PostMapping("/checkout/{username}")
    public ResponseEntity<?> markCheckOut(@PathVariable String username) {
        try {
            AttendanceResponse response = attendanceService.markCheckOut(username);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Check-out recorded successfully. Working hours: " + response.getWorkingHours() + " hrs");
            result.put("attendance", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    // Mark manual attendance
    @PostMapping("/manual")
    public ResponseEntity<?> markManualAttendance(@RequestBody AttendanceRequest request) {
        try {
            AttendanceResponse response = attendanceService.markManualAttendance(request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Manual attendance recorded successfully");
            result.put("attendance", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    // Get user's attendance history
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserAttendance(@PathVariable String username) {
        try {
            List<AttendanceResponse> attendances = attendanceService.getUserAttendance(username);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("attendances", attendances);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    // Get user's monthly attendance
    @GetMapping("/user/{username}/monthly")
    public ResponseEntity<?> getMonthlyAttendance(
            @PathVariable String username,
            @RequestParam int year,
            @RequestParam int month) {
        try {
            List<AttendanceResponse> attendances = attendanceService.getUserAttendanceByMonth(username, year, month);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("attendances", attendances);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    // Get today's attendance for user
    @GetMapping("/today/{username}")
    public ResponseEntity<?> getTodayAttendance(@PathVariable String username) {
        try {
            AttendanceResponse attendance = attendanceService.getTodayAttendance(username);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("attendance", attendance);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    // Get all attendance for a specific date (Admin only)
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<AttendanceResponse> attendances = attendanceService.getAllAttendanceByDate(date);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("attendances", attendances);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    // Get attendance statistics for user
    @GetMapping("/stats/{username}")
    public ResponseEntity<?> getAttendanceStats(@PathVariable String username) {
        try {
            AttendanceService.AttendanceStats stats = attendanceService.getAttendanceStats(username);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("stats", stats);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
}