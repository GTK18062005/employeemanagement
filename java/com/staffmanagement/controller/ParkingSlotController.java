package com.staffmanagement.controller;

import com.staffmanagement.model.ParkingSlot;
import com.staffmanagement.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking/slots")
@CrossOrigin(origins = "http://localhost:3000")
public class ParkingSlotController {
    
    @Autowired
    private ParkingService parkingService;
    
    @PostMapping
    public ResponseEntity<?> createParkingSlot(@RequestBody ParkingSlot slot) {
        try {
            ParkingSlot savedSlot = parkingService.createParkingSlot(slot);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Parking slot created successfully");
            response.put("slot", savedSlot);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{slotId}")
    public ResponseEntity<?> updateParkingSlot(@PathVariable Long slotId, @RequestBody ParkingSlot slotDetails) {
        try {
            ParkingSlot updatedSlot = parkingService.updateParkingSlot(slotId, slotDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Parking slot updated successfully");
            response.put("slot", updatedSlot);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableSlots() {
        try {
            List<ParkingSlot> slots = parkingService.getAvailableSlots();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("slots", slots);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/available/type/{slotType}")
    public ResponseEntity<?> getAvailableSlotsByType(@PathVariable String slotType) {
        try {
            List<ParkingSlot> slots = parkingService.getAvailableSlotsByType(slotType);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("slots", slots);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/available/vehicle/{vehicleType}")
    public ResponseEntity<?> getAvailableSlotsByVehicleType(@PathVariable String vehicleType) {
        try {
            List<ParkingSlot> slots = parkingService.getAvailableSlotsByVehicleType(vehicleType);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("slots", slots);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getParkingStats() {
        try {
            ParkingService.ParkingStats stats = parkingService.getParkingStats();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/maintenance/expire-allocations")
    public ResponseEntity<?> expireOldAllocations() {
        try {
            parkingService.expireOldAllocations();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Expired allocations processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}