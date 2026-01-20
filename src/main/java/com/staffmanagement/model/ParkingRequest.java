package com.staffmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_requests")
public class ParkingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "vehicle_number", nullable = false)
    private String vehicleNumber;

    @Column(name = "vehicle_type", nullable = false)
    private String vehicleType; // CAR, BIKE, SUV, ELECTRIC

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_color")
    private String vehicleColor;

    @Column(name = "preferred_slot_type")
    private String preferredSlotType; // REGULAR, HANDICAP, ELECTRIC

    @Column(name = "request_type", nullable = false)
    private String requestType; // NEW, RENEWAL, CHANGE, TEMPORARY

    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;

    @Column(name = "preferred_start_date")
    private LocalDate preferredStartDate;

    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "status")
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "assigned_slot")
    private String assignedSlot;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Constructors
    public ParkingRequest() {
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.requestDate = LocalDate.now();
        this.status = "PENDING";
        this.requestType = "NEW";
        this.preferredSlotType = "REGULAR";
        this.vehicleType = "CAR";
    }

    public ParkingRequest(String username, String vehicleNumber, String vehicleType) {
        this();
        this.username = username;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleColor() { return vehicleColor; }
    public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }

    public String getPreferredSlotType() { return preferredSlotType; }
    public void setPreferredSlotType(String preferredSlotType) { this.preferredSlotType = preferredSlotType; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }

    public LocalDate getPreferredStartDate() { return preferredStartDate; }
    public void setPreferredStartDate(LocalDate preferredStartDate) { this.preferredStartDate = preferredStartDate; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getAssignedSlot() { return assignedSlot; }
    public void setAssignedSlot(String assignedSlot) { this.assignedSlot = assignedSlot; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}