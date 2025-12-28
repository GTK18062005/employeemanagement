package com.staffmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", unique = true, nullable = false)
    private String username;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "designation")
    private String designation;
    
    @Column(name = "role")
    private String role; // ADMIN, STAFF, PROJECT_MANAGER
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    @Column(name = "bank_account_number")
    private String bankAccountNumber;
    
    @Column(name = "pan_number")
    private String panNumber;
    
    @Column(name = "profile_picture")
    private String profilePicture;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
 // Add these fields to your User.java model

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "vehicle_type")
    private String vehicleType; // CAR, BIKE, SUV, ELECTRIC

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_color")
    private String vehicleColor;

    // Add getters and setters
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleColor() { return vehicleColor; }
    public void setVehicleColor(String vehicleColor) { this.vehicleColor = vehicleColor; }
    // Default constructor
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor for basic profile
    public User(String username, String password, String name, String department, String role) {
        this();
        this.username = username;
        this.password = password;
        this.name = name;
        this.department = department;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    
    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
    
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}