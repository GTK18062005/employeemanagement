package com.staffmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "parking_slots")
public class ParkingSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number", unique = true, nullable = false)
    private String slotNumber; // A-01, B-02, etc.

    @Column(name = "slot_type", nullable = false)
    private String slotType; // REGULAR, HANDICAP, VISITOR, EXECUTIVE

    @Column(name = "floor_level")
    private String floorLevel; // BASEMENT, GROUND, FIRST, SECOND

    @Column(name = "zone")
    private String zone; // A, B, C, D

    @Column(name = "is_covered")
    private Boolean isCovered;

    @Column(name = "has_charging")
    private Boolean hasCharging;

    @Column(name = "status")
    private String status; // AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED

    @Column(name = "vehicle_type")
    private String vehicleType; // CAR, BIKE, SUV, ELECTRIC

    @Column(name = "description")
    private String description;

    @Column(name = "created_date")
    private java.time.LocalDate createdDate;

    @Column(name = "last_updated")
    private java.time.LocalDate lastUpdated;

    // Constructors
    public ParkingSlot() {
        this.createdDate = java.time.LocalDate.now();
        this.lastUpdated = java.time.LocalDate.now();
        this.status = "AVAILABLE";
        this.slotType = "REGULAR";
        this.vehicleType = "CAR";
        this.isCovered = false;
        this.hasCharging = false;
    }

    public ParkingSlot(String slotNumber, String slotType, String floorLevel, String zone) {
        this();
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.floorLevel = floorLevel;
        this.zone = zone;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public String getSlotType() { return slotType; }
    public void setSlotType(String slotType) { this.slotType = slotType; }

    public String getFloorLevel() { return floorLevel; }
    public void setFloorLevel(String floorLevel) { this.floorLevel = floorLevel; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public Boolean getIsCovered() { return isCovered; }
    public void setIsCovered(Boolean isCovered) { this.isCovered = isCovered; }

    public Boolean getHasCharging() { return hasCharging; }
    public void setHasCharging(Boolean hasCharging) { this.hasCharging = hasCharging; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public java.time.LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(java.time.LocalDate createdDate) { this.createdDate = createdDate; }

    public java.time.LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(java.time.LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }
}