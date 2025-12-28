package com.staffmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_allocations")
public class ParkingAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "slot_number", nullable = false)
    private String slotNumber;

    @Column(name = "vehicle_number", nullable = false)
    private String vehicleNumber;

    @Column(name = "vehicle_type")
    private String vehicleType; // CAR, BIKE, SUV, ELECTRIC

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_color")
    private String vehicleColor;

    @Column(name = "allocation_date", nullable = false)
    private LocalDate allocationDate;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "allocation_type", nullable = false)
    private String allocationType; // PERMANENT, TEMPORARY, VISITOR

    @Column(name = "status")
    private String status; // ACTIVE, EXPIRED, CANCELLED

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "notes")
    private String notes;

    // Constructors
    public ParkingAllocation() {
        this.createdDate = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.allocationDate = LocalDate.now();
        this.status = "ACTIVE";
        this.allocationType = "PERMANENT";
    }

    public ParkingAllocation(String username, String slotNumber, String vehicleNumber, LocalDate validFrom) {
        this();
        this.username = username;
        this.slotNumber = slotNumber;
        this.vehicleNumber = vehicleNumber;
        this.validFrom = validFrom;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleColor() { return vehicleColor; }
    public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }

    public LocalDate getAllocationDate() { return allocationDate; }
    public void setAllocationDate(LocalDate allocationDate) { this.allocationDate = allocationDate; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public String getAllocationType() { return allocationType; }
    public void setAllocationType(String allocationType) { this.allocationType = allocationType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDate getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDate approvalDate) { this.approvalDate = approvalDate; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}