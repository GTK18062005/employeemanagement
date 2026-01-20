package com.staffmanagement.service;

import com.staffmanagement.model.ParkingSlot;
import com.staffmanagement.model.ParkingAllocation;
import com.staffmanagement.model.ParkingRequest;
import com.staffmanagement.repository.ParkingSlotRepository;
import com.staffmanagement.repository.ParkingAllocationRepository;
import com.staffmanagement.repository.ParkingRequestRepository;
import com.staffmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ParkingService {
    
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    
    @Autowired
    private ParkingAllocationRepository parkingAllocationRepository;
    
    @Autowired
    private ParkingRequestRepository parkingRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Parking Slot Management
    public ParkingSlot createParkingSlot(ParkingSlot slot) {
        // Check if slot number already exists
        if (parkingSlotRepository.findBySlotNumber(slot.getSlotNumber()).isPresent()) {
            throw new RuntimeException("Parking slot with number " + slot.getSlotNumber() + " already exists");
        }
        
        return parkingSlotRepository.save(slot);
    }
    
    public ParkingSlot updateParkingSlot(Long slotId, ParkingSlot slotDetails) {
        Optional<ParkingSlot> slotOpt = parkingSlotRepository.findById(slotId);
        if (slotOpt.isPresent()) {
            ParkingSlot slot = slotOpt.get();
            
            // Update fields
            if (slotDetails.getSlotType() != null) {
                slot.setSlotType(slotDetails.getSlotType());
            }
            if (slotDetails.getFloorLevel() != null) {
                slot.setFloorLevel(slotDetails.getFloorLevel());
            }
            if (slotDetails.getZone() != null) {
                slot.setZone(slotDetails.getZone());
            }
            if (slotDetails.getIsCovered() != null) {
                slot.setIsCovered(slotDetails.getIsCovered());
            }
            if (slotDetails.getHasCharging() != null) {
                slot.setHasCharging(slotDetails.getHasCharging());
            }
            if (slotDetails.getStatus() != null) {
                slot.setStatus(slotDetails.getStatus());
            }
            if (slotDetails.getVehicleType() != null) {
                slot.setVehicleType(slotDetails.getVehicleType());
            }
            if (slotDetails.getDescription() != null) {
                slot.setDescription(slotDetails.getDescription());
            }
            
            slot.setLastUpdated(LocalDate.now());
            return parkingSlotRepository.save(slot);
        }
        throw new RuntimeException("Parking slot not found");
    }
    
    // Parking Request Management
    public ParkingRequest submitParkingRequest(ParkingRequest request) {
        // Validate user exists
        if (!userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("User not found: " + request.getUsername());
        }
        
        // Check if user already has an active allocation
        Optional<ParkingAllocation> activeAllocation = parkingAllocationRepository
                .findActiveAllocationByUser(request.getUsername(), LocalDate.now());
        if (activeAllocation.isPresent() && "NEW".equals(request.getRequestType())) {
            throw new RuntimeException("User already has an active parking allocation");
        }
        
        // Set default preferred start date if not provided
        if (request.getPreferredStartDate() == null) {
            request.setPreferredStartDate(LocalDate.now().plusDays(1));
        }
        
        return parkingRequestRepository.save(request);
    }
    
    public ParkingRequest approveParkingRequest(Long requestId, String approvedBy, String assignedSlot) {
        Optional<ParkingRequest> requestOpt = parkingRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            ParkingRequest request = requestOpt.get();
            
            // Check if slot is available
            Optional<ParkingSlot> slotOpt = parkingSlotRepository.findBySlotNumber(assignedSlot);
            if (slotOpt.isEmpty()) {
                throw new RuntimeException("Parking slot not found: " + assignedSlot);
            }
            
            ParkingSlot slot = slotOpt.get();
            if (!"AVAILABLE".equals(slot.getStatus())) {
                throw new RuntimeException("Parking slot is not available: " + assignedSlot);
            }
            
            // Update request
            request.setStatus("APPROVED");
            request.setApprovedBy(approvedBy);
            request.setApprovalDate(LocalDate.now());
            request.setAssignedSlot(assignedSlot);
            
            // Create parking allocation
            ParkingAllocation allocation = new ParkingAllocation();
            allocation.setUsername(request.getUsername());
            allocation.setSlotNumber(assignedSlot);
            allocation.setVehicleNumber(request.getVehicleNumber());
            allocation.setVehicleType(request.getVehicleType());
            allocation.setVehicleModel(request.getVehicleModel());
            allocation.setVehicleColor(request.getVehicleColor());
            allocation.setValidFrom(request.getPreferredStartDate());
            allocation.setApprovedBy(approvedBy);
            allocation.setApprovalDate(LocalDate.now());
            allocation.setNotes("Allocation created from approved request #" + requestId);
            
            // Set validity based on request type
            if ("TEMPORARY".equals(request.getRequestType()) && request.getDurationDays() != null) {
                allocation.setValidUntil(request.getPreferredStartDate().plusDays(request.getDurationDays()));
                allocation.setAllocationType("TEMPORARY");
            } else {
                allocation.setAllocationType("PERMANENT");
            }
            
            // Update slot status
            slot.setStatus("OCCUPIED");
            parkingSlotRepository.save(slot);
            
            // Save allocation
            parkingAllocationRepository.save(allocation);
            
            return parkingRequestRepository.save(request);
        }
        throw new RuntimeException("Parking request not found");
    }
    
    public ParkingRequest rejectParkingRequest(Long requestId, String rejectedBy, String reason) {
        Optional<ParkingRequest> requestOpt = parkingRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            ParkingRequest request = requestOpt.get();
            request.setStatus("REJECTED");
            request.setApprovedBy(rejectedBy);
            request.setApprovalDate(LocalDate.now());
            request.setRejectionReason(reason);
            return parkingRequestRepository.save(request);
        }
        throw new RuntimeException("Parking request not found");
    }
    
    // Parking Allocation Management
    public void deallocateParkingSlot(String username) {
        Optional<ParkingAllocation> allocationOpt = parkingAllocationRepository
                .findActiveAllocationByUser(username, LocalDate.now());
        
        if (allocationOpt.isPresent()) {
            ParkingAllocation allocation = allocationOpt.get();
            allocation.setStatus("CANCELLED");
            allocation.setLastUpdated(java.time.LocalDateTime.now());
            parkingAllocationRepository.save(allocation);
            
            // Free up the parking slot
            Optional<ParkingSlot> slotOpt = parkingSlotRepository.findBySlotNumber(allocation.getSlotNumber());
            if (slotOpt.isPresent()) {
                ParkingSlot slot = slotOpt.get();
                slot.setStatus("AVAILABLE");
                parkingSlotRepository.save(slot);
            }
        } else {
            throw new RuntimeException("No active parking allocation found for user: " + username);
        }
    }
    
    public void renewParkingAllocation(String username, LocalDate newValidUntil) {
        Optional<ParkingAllocation> allocationOpt = parkingAllocationRepository
                .findActiveAllocationByUser(username, LocalDate.now());
        
        if (allocationOpt.isPresent()) {
            ParkingAllocation allocation = allocationOpt.get();
            allocation.setValidUntil(newValidUntil);
            allocation.setLastUpdated(java.time.LocalDateTime.now());
            parkingAllocationRepository.save(allocation);
        } else {
            throw new RuntimeException("No active parking allocation found for user: " + username);
        }
    }
    
    // Query Methods
    public List<ParkingSlot> getAvailableSlots() {
        return parkingSlotRepository.findByStatusOrderBySlotNumber("AVAILABLE");
    }
    
    public List<ParkingSlot> getAvailableSlotsByType(String slotType) {
        return parkingSlotRepository.findAvailableSlotsByType(slotType);
    }
    
    public List<ParkingSlot> getAvailableSlotsByVehicleType(String vehicleType) {
        return parkingSlotRepository.findAvailableSlotsByVehicleType(vehicleType);
    }
    
    public ParkingAllocation getUserAllocation(String username) {
        return parkingAllocationRepository.findActiveAllocationByUser(username, LocalDate.now())
                .orElse(null);
    }
    
    public List<ParkingRequest> getUserRequests(String username) {
        return parkingRequestRepository.findByUsernameOrderByCreatedDateDesc(username);
    }
    
    public List<ParkingRequest> getPendingRequests() {
        return parkingRequestRepository.findByStatusOrderByCreatedDateDesc("PENDING");
    }
    
    public List<ParkingAllocation> getActiveAllocations() {
        return parkingAllocationRepository.findActiveAllocations(LocalDate.now());
    }
    
    // Statistics
    public ParkingStats getParkingStats() {
        ParkingStats stats = new ParkingStats();
        
        stats.setTotalSlots(parkingSlotRepository.count());
        stats.setAvailableSlots(parkingSlotRepository.countAvailableSlots());
        stats.setOccupiedSlots(parkingSlotRepository.countOccupiedSlots());
        stats.setPendingRequests(parkingRequestRepository.countPendingRequests());
        stats.setActiveAllocations(parkingAllocationRepository.countByStatus("ACTIVE"));
        
        // Zone-wise statistics
        List<Object[]> zoneStats = parkingSlotRepository.countSlotsByZone();
        stats.setZoneStats(zoneStats);
        
        // Floor-wise statistics
        List<Object[]> floorStats = parkingSlotRepository.countSlotsByFloor();
        stats.setFloorStats(floorStats);
        
        return stats;
    }
    
    // Auto-expire allocations
    @Transactional
    public void expireOldAllocations() {
        List<ParkingAllocation> expiredAllocations = parkingAllocationRepository
                .findExpiredAllocations(LocalDate.now());
        
        for (ParkingAllocation allocation : expiredAllocations) {
            allocation.setStatus("EXPIRED");
            allocation.setLastUpdated(java.time.LocalDateTime.now());
            parkingAllocationRepository.save(allocation);
            
            // Free up the parking slot
            Optional<ParkingSlot> slotOpt = parkingSlotRepository.findBySlotNumber(allocation.getSlotNumber());
            if (slotOpt.isPresent()) {
                ParkingSlot slot = slotOpt.get();
                slot.setStatus("AVAILABLE");
                parkingSlotRepository.save(slot);
            }
        }
    }
    
    // Inner class for parking statistics
    public static class ParkingStats {
        private long totalSlots;
        private long availableSlots;
        private long occupiedSlots;
        private long pendingRequests;
        private long activeAllocations;
        private List<Object[]> zoneStats;
        private List<Object[]> floorStats;
        
        // Getters and Setters
        public long getTotalSlots() { return totalSlots; }
        public void setTotalSlots(long totalSlots) { this.totalSlots = totalSlots; }
        
        public long getAvailableSlots() { return availableSlots; }
        public void setAvailableSlots(long availableSlots) { this.availableSlots = availableSlots; }
        
        public long getOccupiedSlots() { return occupiedSlots; }
        public void setOccupiedSlots(long occupiedSlots) { this.occupiedSlots = occupiedSlots; }
        
        public long getPendingRequests() { return pendingRequests; }
        public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }
        
        public long getActiveAllocations() { return activeAllocations; }
        public void setActiveAllocations(long activeAllocations) { this.activeAllocations = activeAllocations; }
        
        public List<Object[]> getZoneStats() { return zoneStats; }
        public void setZoneStats(List<Object[]> zoneStats) { this.zoneStats = zoneStats; }
        
        public List<Object[]> getFloorStats() { return floorStats; }
        public void setFloorStats(List<Object[]> floorStats) { this.floorStats = floorStats; }
    }
}