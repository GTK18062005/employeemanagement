package com.staffmanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Entity
@Table(name = "salaries")
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "salary_month", nullable = false)
    private YearMonth salaryMonth;

    @Column(name = "basic_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "house_rent_allowance", precision = 10, scale = 2)
    private BigDecimal houseRentAllowance;

    @Column(name = "travel_allowance", precision = 10, scale = 2)
    private BigDecimal travelAllowance;

    @Column(name = "medical_allowance", precision = 10, scale = 2)
    private BigDecimal medicalAllowance;

    @Column(name = "bonus", precision = 10, scale = 2)
    private BigDecimal bonus;

    @Column(name = "overtime_hours")
    private Double overtimeHours;

    @Column(name = "overtime_rate", precision = 10, scale = 2)
    private BigDecimal overtimeRate;

    @Column(name = "overtime_pay", precision = 10, scale = 2)
    private BigDecimal overtimePay;

    @Column(name = "tax_deduction", precision = 10, scale = 2)
    private BigDecimal taxDeduction;

    @Column(name = "provident_fund", precision = 10, scale = 2)
    private BigDecimal providentFund;

    @Column(name = "other_deductions", precision = 10, scale = 2)
    private BigDecimal otherDeductions;

    @Column(name = "net_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal netSalary;

    @Column(name = "gross_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal grossSalary;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "payment_status")
    private String paymentStatus; // PENDING, PAID, FAILED

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    // Constructors
    public Salary() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.paymentStatus = "PENDING";
    }

    public Salary(String username, YearMonth salaryMonth, BigDecimal basicSalary) {
        this();
        this.username = username;
        this.salaryMonth = salaryMonth;
        this.basicSalary = basicSalary;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public BigDecimal getOvertimePay() { return overtimePay; }
    public void setOvertimePay(BigDecimal overtimePay) { this.overtimePay = overtimePay; }

    public BigDecimal getTaxDeduction() { return taxDeduction; }
    public void setTaxDeduction(BigDecimal taxDeduction) { this.taxDeduction = taxDeduction; }

    public BigDecimal getProvidentFund() { return providentFund; }
    public void setProvidentFund(BigDecimal providentFund) { this.providentFund = providentFund; }

    public BigDecimal getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(BigDecimal otherDeductions) { this.otherDeductions = otherDeductions; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public BigDecimal getGrossSalary() { return grossSalary; }
    public void setGrossSalary(BigDecimal grossSalary) { this.grossSalary = grossSalary; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
}