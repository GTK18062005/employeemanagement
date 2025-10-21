package com.staffmanagement.config;

import com.staffmanagement.model.User;
import com.staffmanagement.model.Salary;
import com.staffmanagement.repository.AttendanceRepository;
import com.staffmanagement.repository.UserRepository;
import com.staffmanagement.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.YearMonth;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing application data...");
        
        // Clear existing data to avoid conflicts
        clearExistingData();
        
        // Create users
        createSampleUsers();
        
        // Create sample salaries
        createSampleSalaries();
        
        System.out.println("Data initialization completed successfully!");
    }
    
    private void clearExistingData() {
        try {
            // Clear in correct order to avoid foreign key constraints
            attendanceRepository.deleteAll();
            salaryRepository.deleteAll();
            userRepository.deleteAll();
            
            System.out.println("Cleared all existing data");
            
            // Reset sequences for H2
            resetSequences();
            
        } catch (Exception e) {
            System.out.println("No existing data to clear or error clearing: " + e.getMessage());
        }
    }
    
    private void resetSequences() {
        try {
            // Reset H2 sequences - this is H2 specific
            userRepository.resetUserSequence();
            attendanceRepository.resetAttendanceSequence();
            salaryRepository.resetSalarySequence();
            System.out.println("Reset database sequences");
        } catch (Exception e) {
            System.out.println("Could not reset sequences: " + e.getMessage());
        }
    }
    
    private void createSampleUsers() {
        // Admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            admin.setName("Admin User");
            admin.setEmail("admin@company.com");
            admin.setRole("ADMIN");
            admin.setDepartment("Administration");
            admin.setDesignation("System Administrator");
            admin.setPhone("+1234567890");
            admin.setBankAccountNumber("ACC00123456");
            userRepository.save(admin);
            System.out.println("Created admin user");
        }

        // Staff user
        if (!userRepository.existsByUsername("staff")) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword("staff123");
            staff.setName("John Doe");
            staff.setEmail("john.doe@company.com");
            staff.setPhone("+1122334455");
            staff.setDepartment("HR");
            staff.setDesignation("HR Manager");
            staff.setRole("STAFF");
            staff.setAddress("456 Staff Avenue");
            staff.setEmergencyContact("+5566778899");
            staff.setBankAccountNumber("ACC00987654");
            staff.setPanNumber("FGHTJ5678K");
            userRepository.save(staff);
            System.out.println("Created staff user");
        }

        // Additional sample user
        if (!userRepository.existsByUsername("alice.smith")) {
            User alice = new User();
            alice.setUsername("alice.smith");
            alice.setPassword("alice123");
            alice.setName("Alice Smith");
            alice.setEmail("alice.smith@company.com");
            alice.setPhone("+2233445566");
            alice.setDepartment("IT");
            alice.setDesignation("Software Developer");
            alice.setRole("STAFF");
            alice.setBankAccountNumber("ACC00543210");
            userRepository.save(alice);
            System.out.println("Created sample user: Alice Smith");
        }
    }
    
    private void createSampleSalaries() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        
        // Create sample salary for staff
        if (!salaryRepository.existsByUsernameAndSalaryMonth("staff", lastMonth)) {
            Salary staffSalary = new Salary();
            staffSalary.setUsername("staff");
            staffSalary.setSalaryMonth(lastMonth);
            staffSalary.setBasicSalary(new BigDecimal("45000.00"));
            staffSalary.setHouseRentAllowance(new BigDecimal("18000.00"));
            staffSalary.setTravelAllowance(new BigDecimal("1600.00"));
            staffSalary.setMedicalAllowance(new BigDecimal("1250.00"));
            staffSalary.setBonus(new BigDecimal("5000.00"));
            staffSalary.setOvertimeHours(8.0);
            staffSalary.setOvertimeRate(new BigDecimal("200.00"));
            staffSalary.setOvertimePay(new BigDecimal("1600.00"));
            staffSalary.setTaxDeduction(new BigDecimal("2250.00"));
            staffSalary.setProvidentFund(new BigDecimal("5400.00"));
            staffSalary.setOtherDeductions(new BigDecimal("500.00"));
            staffSalary.setGrossSalary(new BigDecimal("72450.00"));
            staffSalary.setNetSalary(new BigDecimal("64300.00"));
            staffSalary.setPaymentStatus("PAID");
            staffSalary.setPaymentDate(java.time.LocalDate.now().minusDays(5));
            staffSalary.setBankAccountNumber("ACC00987654");
            
            salaryRepository.save(staffSalary);
            System.out.println("Sample salary created for staff");
        }
        
        // Create sample salary for alice
        if (!salaryRepository.existsByUsernameAndSalaryMonth("alice.smith", lastMonth)) {
            Salary aliceSalary = new Salary();
            aliceSalary.setUsername("alice.smith");
            aliceSalary.setSalaryMonth(lastMonth);
            aliceSalary.setBasicSalary(new BigDecimal("50000.00"));
            aliceSalary.setHouseRentAllowance(new BigDecimal("20000.00"));
            aliceSalary.setTravelAllowance(new BigDecimal("1600.00"));
            aliceSalary.setMedicalAllowance(new BigDecimal("1250.00"));
            aliceSalary.setBonus(new BigDecimal("3000.00"));
            aliceSalary.setOvertimeHours(5.0);
            aliceSalary.setOvertimeRate(new BigDecimal("200.00"));
            aliceSalary.setOvertimePay(new BigDecimal("1000.00"));
            aliceSalary.setTaxDeduction(new BigDecimal("2500.00"));
            aliceSalary.setProvidentFund(new BigDecimal("6000.00"));
            aliceSalary.setOtherDeductions(new BigDecimal("400.00"));
            aliceSalary.setGrossSalary(new BigDecimal("77850.00"));
            aliceSalary.setNetSalary(new BigDecimal("68950.00"));
            aliceSalary.setPaymentStatus("PAID");
            aliceSalary.setPaymentDate(java.time.LocalDate.now().minusDays(5));
            aliceSalary.setBankAccountNumber("ACC00543210");
            
            salaryRepository.save(aliceSalary);
            System.out.println("Sample salary created for alice.smith");
        }
    }
}