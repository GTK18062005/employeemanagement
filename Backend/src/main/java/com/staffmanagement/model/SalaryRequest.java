package com.staffmanagement.model;

import java.math.BigDecimal;
import java.time.YearMonth;

public class SalaryRequest {
    private String username;
    private YearMonth salaryMonth;
    private BigDecimal basicSalary;
    private BigDecimal houseRentAllowance;
    private BigDecimal travelAllowance;
    private BigDecimal medicalAllowance;
    private BigDecimal bonus;
    private Double overtimeHours;
    private BigDecimal overtimeRate;
    private BigDecimal taxDeduction;
    private BigDecimal providentFund;
    private BigDecimal otherDeductions;
    private String notes;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public YearMonth getSalaryMonth() { return salaryMonth; }
    public void setSalaryMonth(YearMonth salaryMonth) { this.salaryMonth = salaryMonth; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public BigDecimal getHouseRentAllowance() { return houseRentAllowance; }
    public void setHouseRentAllowance(BigDecimal houseRentAllowance) { this.houseRentAllowance = houseRentAllowance; }

    public BigDecimal getTravelAllowance() { return travelAllowance; }
    public void setTravelAllowance(BigDecimal travelAllowance) { this.travelAllowance = travelAllowance; }

    public BigDecimal getMedicalAllowance() { return medicalAllowance; }
    public void setMedicalAllowance(BigDecimal medicalAllowance) { this.medicalAllowance = medicalAllowance; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public Double getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(Double overtimeHours) { this.overtimeHours = overtimeHours; }

    public BigDecimal getOvertimeRate() { return overtimeRate; }
    public void setOvertimeRate(BigDecimal overtimeRate) { this.overtimeRate = overtimeRate; }

    public BigDecimal getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(BigDecimal taxDeduction) { this.taxDeduction = taxDeduction; }

    public BigDecimal getProvidentFund() { return providentFund; }
    public void setProvidentFund(BigDecimal providentFund) { this.providentFund = providentFund; }

    public BigDecimal getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(BigDecimal otherDeductions) { this.otherDeductions = otherDeductions; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}