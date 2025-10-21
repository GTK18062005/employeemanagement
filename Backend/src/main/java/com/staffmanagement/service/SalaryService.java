package com.staffmanagement.service;

import com.staffmanagement.model.Salary;
import com.staffmanagement.model.SalaryRequest;
import com.staffmanagement.model.SalaryResponse;
import com.staffmanagement.model.User;
import com.staffmanagement.repository.SalaryRepository;
import com.staffmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SalaryService {
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Default salary components (can be configured)
    private static final BigDecimal HRA_PERCENTAGE = new BigDecimal("0.40"); // 40% of basic
    private static final BigDecimal TRAVEL_ALLOWANCE = new BigDecimal("1600.00");
    private static final BigDecimal MEDICAL_ALLOWANCE = new BigDecimal("1250.00");
    private static final BigDecimal OVERTIME_RATE = new BigDecimal("200.00"); // per hour
    private static final BigDecimal PF_PERCENTAGE = new BigDecimal("0.12"); // 12% of basic
    private static final BigDecimal TAX_THRESHOLD = new BigDecimal("500000.00"); // Annual tax threshold
    private static final BigDecimal TAX_RATE = new BigDecimal("0.05"); // 5% above threshold
    
    public SalaryResponse calculateAndSaveSalary(SalaryRequest request) {
        // Validate request
        if (request == null) {
            throw new IllegalArgumentException("Salary request cannot be null");
        }
        
        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        // Check if salary already exists for this month
        if (salaryRepository.existsByUsernameAndSalaryMonth(username, request.getSalaryMonth())) {
            throw new RuntimeException("Salary already calculated for " + username + " for month " + request.getSalaryMonth());
        }
        
        // Get user details
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Salary salary = new Salary();
        salary.setUsername(username);
        salary.setSalaryMonth(request.getSalaryMonth());
        
        // Set basic salary from request or use default based on designation
        BigDecimal basicSalary = request.getBasicSalary();
        if (basicSalary == null) {
            basicSalary = getDefaultBasicSalary(user.getDesignation());
        }
        salary.setBasicSalary(basicSalary);
        
        // Calculate allowances
        salary.setHouseRentAllowance(calculateHRA(basicSalary, request.getHouseRentAllowance()));
        salary.setTravelAllowance(calculateTravelAllowance(request.getTravelAllowance()));
        salary.setMedicalAllowance(calculateMedicalAllowance(request.getMedicalAllowance()));
        
        // Set bonus
        salary.setBonus(request.getBonus() != null ? request.getBonus() : BigDecimal.ZERO);
        
        // Calculate overtime
        salary.setOvertimeHours(request.getOvertimeHours() != null ? request.getOvertimeHours() : 0.0);
        salary.setOvertimeRate(request.getOvertimeRate() != null ? request.getOvertimeRate() : OVERTIME_RATE);
        salary.setOvertimePay(calculateOvertimePay(salary.getOvertimeHours(), salary.getOvertimeRate()));
        
        // Calculate gross salary
        BigDecimal grossSalary = calculateGrossSalary(salary);
        salary.setGrossSalary(grossSalary);
        
        // Calculate deductions
        salary.setProvidentFund(calculatePF(basicSalary, request.getProvidentFund()));
        salary.setTaxDeduction(calculateTaxDeduction(grossSalary, request.getTaxDeduction()));
        salary.setOtherDeductions(request.getOtherDeductions() != null ? request.getOtherDeductions() : BigDecimal.ZERO);
        
        // Calculate net salary
        BigDecimal netSalary = calculateNetSalary(salary);
        salary.setNetSalary(netSalary);
        
        // Set bank account and notes
        salary.setBankAccountNumber(user.getBankAccountNumber());
        salary.setNotes(request.getNotes());
        
        Salary savedSalary = salaryRepository.save(salary);
        return convertToResponse(savedSalary, user);
    }
    
    public SalaryResponse getSalary(Long salaryId) {
        Optional<Salary> salaryOpt = salaryRepository.findById(salaryId);
        if (salaryOpt.isEmpty()) {
            throw new RuntimeException("Salary record not found");
        }
        
        Salary salary = salaryOpt.get();
        User user = userRepository.findByUsername(salary.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + salary.getUsername()));
        
        return convertToResponse(salary, user);
    }
    
    public SalaryResponse getSalaryByUserAndMonth(String username, YearMonth salaryMonth) {
        Optional<Salary> salaryOpt = salaryRepository.findByUsernameAndSalaryMonth(username, salaryMonth);
        if (salaryOpt.isEmpty()) {
            return null;
        }
        
        Salary salary = salaryOpt.get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return convertToResponse(salary, user);
    }
    
    public List<SalaryResponse> getUserSalaries(String username) {
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        List<Salary> salaries = salaryRepository.findByUsernameOrderBySalaryMonthDesc(username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        return salaries.stream()
                .map(salary -> convertToResponse(salary, user))
                .collect(Collectors.toList());
    }
    
    public List<SalaryResponse> getSalariesByMonth(YearMonth salaryMonth) {
        List<Salary> salaries = salaryRepository.findBySalaryMonthOrderByUsername(salaryMonth);
        
        return salaries.stream()
                .map(salary -> {
                    User user = userRepository.findByUsername(salary.getUsername())
                            .orElse(null); // Return null if user not found, handle in convertToResponse
                    return convertToResponse(salary, user);
                })
                .collect(Collectors.toList());
    }
    
    public SalaryResponse updateSalaryStatus(Long salaryId, String status) {
        Optional<Salary> salaryOpt = salaryRepository.findById(salaryId);
        if (salaryOpt.isEmpty()) {
            throw new RuntimeException("Salary record not found");
        }
        
        Salary salary = salaryOpt.get();
        salary.setPaymentStatus(status);
        
        if ("PAID".equals(status)) {
            salary.setPaymentDate(java.time.LocalDate.now());
        }
        
        salary.setUpdatedAt(java.time.LocalDate.now());
        Salary updatedSalary = salaryRepository.save(salary);
        
        User user = userRepository.findByUsername(updatedSalary.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + updatedSalary.getUsername()));
        return convertToResponse(updatedSalary, user);
    }
    
    public SalaryResponse getLatestSalary(String username) {
        // Validate username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        // Use the default method we created
        Optional<Salary> salaryOpt = salaryRepository.findMostRecentSalaryByUsername(username);
        if (salaryOpt.isEmpty()) {
            return null;
        }
        
        Salary salary = salaryOpt.get();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return convertToResponse(salary, user);
    }
    
    // Private helper methods for calculations
    private BigDecimal getDefaultBasicSalary(String designation) {
        // Default basic salaries based on designation
        switch (designation != null ? designation.toUpperCase() : "STAFF") {
            case "HR MANAGER":
                return new BigDecimal("60000.00");
            case "SENIOR DEVELOPER":
                return new BigDecimal("55000.00");
            case "SOFTWARE DEVELOPER":
                return new BigDecimal("45000.00");
            case "ACCOUNTANT":
                return new BigDecimal("40000.00");
            case "SALES MANAGER":
                return new BigDecimal("50000.00");
            case "MARKETING MANAGER":
                return new BigDecimal("48000.00");
            default:
                return new BigDecimal("35000.00"); // Default for STAFF
        }
    }
    
    private BigDecimal calculateHRA(BigDecimal basicSalary, BigDecimal customHRA) {
        return customHRA != null ? customHRA : basicSalary.multiply(HRA_PERCENTAGE);
    }
    
    private BigDecimal calculateTravelAllowance(BigDecimal customTA) {
        return customTA != null ? customTA : TRAVEL_ALLOWANCE;
    }
    
    private BigDecimal calculateMedicalAllowance(BigDecimal customMA) {
        return customMA != null ? customMA : MEDICAL_ALLOWANCE;
    }
    
    private BigDecimal calculateOvertimePay(Double overtimeHours, BigDecimal overtimeRate) {
        return overtimeRate.multiply(BigDecimal.valueOf(overtimeHours))
                          .setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateGrossSalary(Salary salary) {
        return salary.getBasicSalary()
                .add(salary.getHouseRentAllowance())
                .add(salary.getTravelAllowance())
                .add(salary.getMedicalAllowance())
                .add(salary.getBonus())
                .add(salary.getOvertimePay())
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculatePF(BigDecimal basicSalary, BigDecimal customPF) {
        return customPF != null ? customPF : basicSalary.multiply(PF_PERCENTAGE)
                                                        .setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateTaxDeduction(BigDecimal grossSalary, BigDecimal customTax) {
        if (customTax != null) {
            return customTax;
        }
        
        // Simple tax calculation (5% of annual salary above threshold)
        BigDecimal annualSalary = grossSalary.multiply(new BigDecimal("12"));
        if (annualSalary.compareTo(TAX_THRESHOLD) > 0) {
            BigDecimal taxableAmount = annualSalary.subtract(TAX_THRESHOLD);
            return taxableAmount.multiply(TAX_RATE)
                              .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP); // Monthly tax
        }
        
        return BigDecimal.ZERO;
    }
    
    private BigDecimal calculateNetSalary(Salary salary) {
        return salary.getGrossSalary()
                .subtract(salary.getTaxDeduction())
                .subtract(salary.getProvidentFund())
                .subtract(salary.getOtherDeductions())
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    private SalaryResponse convertToResponse(Salary salary, User user) {
        SalaryResponse response = new SalaryResponse();
        response.setId(salary.getId());
        response.setUsername(salary.getUsername());
        response.setEmployeeName(user != null ? user.getName() : "N/A");
        response.setDepartment(user != null ? user.getDepartment() : "N/A");
        response.setSalaryMonth(salary.getSalaryMonth());
        response.setBasicSalary(salary.getBasicSalary());
        response.setHouseRentAllowance(salary.getHouseRentAllowance());
        response.setTravelAllowance(salary.getTravelAllowance());
        response.setMedicalAllowance(salary.getMedicalAllowance());
        response.setBonus(salary.getBonus());
        response.setOvertimeHours(salary.getOvertimeHours());
        response.setOvertimeRate(salary.getOvertimeRate());
        response.setOvertimePay(salary.getOvertimePay());
        response.setTaxDeduction(salary.getTaxDeduction());
        response.setProvidentFund(salary.getProvidentFund());
        response.setOtherDeductions(salary.getOtherDeductions());
        response.setGrossSalary(salary.getGrossSalary());
        response.setNetSalary(salary.getNetSalary());
        response.setPaymentDate(salary.getPaymentDate());
        response.setPaymentStatus(salary.getPaymentStatus());
        response.setBankAccountNumber(salary.getBankAccountNumber());
        response.setNotes(salary.getNotes());
        
        return response;
    }
}