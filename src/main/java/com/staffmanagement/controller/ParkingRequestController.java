package com.staffmanagement.controller;

import com.staffmanagement.model.ParkingRequest;
import com.staffmanagement.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking/requests")
@CrossOrigin(origins = "http://localhost:3000")
public class ParkingRequestController {
    
    @Autowired
    private ParkingService parkingService;
    
    @PostMapping
    public ResponseEntity<?> submitParkingRequest(@RequestBody ParkingRequest request) {
        try {
            ParkingRequest savedRequest = parkingService.submitParkingRequest(request);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Parking request submitted successfully");
            response.put("request", savedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<?> approveParkingRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> approvalRequest) {
        try {
            String approvedBy = approvalRequest.get("approvedBy");
            String assignedSlot = approvalRequest.get("assignedSlot");
            
            ParkingRequest approvedRequest = parkingService.approveParkingRequest(requestId, approvedBy, assignedSlot);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Parking request approved successfully");
            response.put("request", approvedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectParkingRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> rejectionRequest) {
        try {
            String rejectedBy = rejectionRequest.get("rejectedBy");
            String reason = rejectionRequest.get("reason");
            
            ParkingRequest rejectedRequest = parkingService.rejectParkingRequest(requestId, rejectedBy, reason);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Parking request rejected");
            response.put("request", rejectedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserRequests(@PathVariable String username) {
        try {
            List<ParkingRequest> requests = parkingService.getUserRequests(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("requests", requests);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests() {
        try {
            List<ParkingRequest> requests = parkingService.getPendingRequests();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("requests", requests);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}