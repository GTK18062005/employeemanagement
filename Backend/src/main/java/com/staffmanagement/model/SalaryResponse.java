package com.staffmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class SalaryResponse {
    private Long id;
    private String username;
    private String employeeName;
    private String department;
    private YearMonth salaryMonth;
    private BigDecimal basicSalary;
    private BigDecimal houseRentAllowance;
    private BigDecimal travelAllowance;
    private BigDecimal medicalAllowance;
    private BigDecimal bonus;
    private Double overtimeHours;
    private BigDecimal overtimeRate;
    private BigDecimal overtimePay;
    private BigDecimal taxDeduction;
    private BigDecimal providentFund;
    private BigDecimal otherDeductions;
    private BigDecimal grossSalary;
    private BigDecimal netSalary;
    private LocalDate paymentDate;
    private String paymentStatus;
    private String bankAccountNumber;
    private String notes;

    // Getters and Setters
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

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

    public BigDecimal getOvertimePay() { return overtimePay; }
    public void setOvertimePay(BigDecimal overtimePay) { this.overtimePay = overtimePay; }

    public BigDecimal getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(BigDecimal taxDeduction) { this.taxDeduction = taxDeduction; }

    public BigDecimal getProvidentFund() { return providentFund; }
    public void setProvidentFund(BigDecimal providentFund) { this.providentFund = providentFund; }

    public BigDecimal getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(BigDecimal otherDeductions) { this.otherDeductions = otherDeductions; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}