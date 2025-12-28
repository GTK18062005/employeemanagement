package com.staffmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leaves")
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "leave_type", nullable = false)
    private String leaveType; // SICK, CASUAL, EARNED, MATERNITY, PATERNITY

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "number_of_days", nullable = false)
    private Integer numberOfDays;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "status")
    private String status; // PENDING, APPROVED, REJECTED

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "applied_date")
    private LocalDate appliedDate;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "handover_notes", length = 1000)
    private String handoverNotes;

    // Constructors
    public Leave() {
        this.appliedDate = LocalDate.now();
        this.status = "PENDING";
    }

    public Leave(String username, String leaveType, LocalDate startDate, LocalDate endDate, String reason) {
        this();
        this.username = username;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(Integer numberOfDays) { this.numberOfDays = numberOfDays; }

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

    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }
}