package com.staffmanagement.model;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_policies")
public class LeavePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leave_type", unique = true, nullable = false)
    private String leaveType; // SICK, CASUAL, EARNED, MATERNITY, PATERNITY

    @Column(name = "annual_entitlement", nullable = false)
    private Integer annualEntitlement;

    @Column(name = "max_consecutive_days")
    private Integer maxConsecutiveDays;

    @Column(name = "advance_notice_days")
    private Integer advanceNoticeDays;

    @Column(name = "requires_approval")
    private Boolean requiresApproval;

    @Column(name = "carry_forward_limit")
    private Integer carryForwardLimit;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    // Constructors
    public LeavePolicy() {
        this.isActive = true;
    }

    public LeavePolicy(String leaveType, Integer annualEntitlement) {
        this();
        this.leaveType = leaveType;
        this.annualEntitlement = annualEntitlement;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public Integer getAnnualEntitlement() { return annualEntitlement; }
    public void setAnnualEntitlement(Integer annualEntitlement) { this.annualEntitlement = annualEntitlement; }

    public Integer getMaxConsecutiveDays() { return maxConsecutiveDays; }
    public void setMaxConsecutiveDays(Integer maxConsecutiveDays) { this.maxConsecutiveDays = maxConsecutiveDays; }

    public Integer getAdvanceNoticeDays() { return advanceNoticeDays; }
    public void setAdvanceNoticeDays(Integer advanceNoticeDays) { this.advanceNoticeDays = advanceNoticeDays; }

    public Boolean getRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(Boolean requiresApproval) { this.requiresApproval = requiresApproval; }

    public Integer getCarryForwardLimit() { return carryForwardLimit; }
    public void setCarryForwardLimit(Integer carryForwardLimit) { this.carryForwardLimit = carryForwardLimit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}