package com.staffmanagement.controller;

import com.staffmanagement.service.ParkingService;
import com.staffmanagement.model.ParkingAllocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking/allocations")
@CrossOrigin(origins = "http://localhost:3000")
public class ParkingAllocationController {
    
    @Autowired
    private ParkingService parkingService;
    
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserAllocation(@PathVariable String username) {
        try {
            ParkingAllocation allocation = parkingService.getUserAllocation(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("allocation", allocation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/active")
    public ResponseEntity<?> getActiveAllocations() {
        try {
            // Remove the cast - use the proper type
            List<ParkingAllocation> allocations = parkingService.getActiveAllocations();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("allocations", allocations);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/user/{username}")
    public ResponseEntity<?> deallocateParkingSlot(@PathVariable String username) {
        try {
            parkingService.deallocateParkingSlot(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Parking slot deallocated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/user/{username}/renew")
    public ResponseEntity<?> renewParkingAllocation(
            @PathVariable String username,
            @RequestBody Map<String, String> renewalRequest) {
        try {
            LocalDate newValidUntil = LocalDate.parse(renewalRequest.get("newValidUntil"));
            parkingService.renewParkingAllocation(username, newValidUntil);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Parking allocation renewed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}