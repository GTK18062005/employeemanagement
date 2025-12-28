package com.staffmanagement.model;

import java.time.LocalTime;

public class AttendanceRequest {
    private String username;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String status;
    private String notes;
    
    // Constructors
    public AttendanceRequest() {}
    
    public AttendanceRequest(String username, LocalTime checkInTime, LocalTime checkOutTime, String status) {
        this.username = username;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.status = status;
    }
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public LocalTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalTime checkInTime) { this.checkInTime = checkInTime; }
    
    public LocalTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalTime checkOutTime) { this.checkOutTime = checkOutTime; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}