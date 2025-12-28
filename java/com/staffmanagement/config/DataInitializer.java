package com.staffmanagement.config;

import com.staffmanagement.model.*;
import com.staffmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private LeavePolicyRepository leavePolicyRepository;
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    
    @Autowired
    private ParkingAllocationRepository parkingAllocationRepository;
    
    @Autowired
    private ParkingRequestRepository parkingRequestRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing application data...");
        
        // Clear existing data to avoid conflicts
        clearExistingData();
        
        // Create users
        createSampleUsers();
        
        // Create sample salaries
        createSampleSalaries();
        
        // Create leave policies
        createLeavePolicies();
        
        // Create sample projects
        createSampleProjects();
        
        // Create parking slots and allocations
        createParkingSlots();
        createSampleParkingAllocations();
        
        System.out.println("Data initialization completed successfully!");
    }
    
    private void clearExistingData() {
        try {
            // Clear in correct order to avoid foreign key constraints
            attendanceRepository.deleteAll();
            salaryRepository.deleteAll();
            parkingAllocationRepository.deleteAll();
            parkingRequestRepository.deleteAll();
            parkingSlotRepository.deleteAll();
            projectRepository.deleteAll();
            leavePolicyRepository.deleteAll();
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
            // Reset H2 sequences
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

        // Project Manager user
        if (!userRepository.existsByUsername("pm.user")) {
            User pm = new User();
            pm.setUsername("pm.user");
            pm.setPassword("pm123");
            pm.setName("Project Manager");
            pm.setEmail("pm@company.com");
            pm.setPhone("+1987654321");
            pm.setDepartment("IT");
            pm.setDesignation("Project Manager");
            pm.setRole("PROJECT_MANAGER");
            pm.setAddress("789 Manager Street");
            pm.setEmergencyContact("+1122334455");
            pm.setBankAccountNumber("ACC00998877");
            userRepository.save(pm);
            System.out.println("Created project manager user");
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
        
        // Additional developer
        if (!userRepository.existsByUsername("bob.johnson")) {
            User bob = new User();
            bob.setUsername("bob.johnson");
            bob.setPassword("bob123");
            bob.setName("Bob Johnson");
            bob.setEmail("bob.johnson@company.com");
            bob.setPhone("+3344556677");
            bob.setDepartment("IT");
            bob.setDesignation("Senior Developer");
            bob.setRole("STAFF");
            userRepository.save(bob);
            System.out.println("Created sample user: Bob Johnson");
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
    
    private void createLeavePolicies() {
        // Sick Leave Policy
        if (!leavePolicyRepository.findByLeaveType("SICK").isPresent()) {
            LeavePolicy sickLeave = new LeavePolicy();
            sickLeave.setLeaveType("SICK");
            sickLeave.setAnnualEntitlement(12);
            sickLeave.setMaxConsecutiveDays(5);
            sickLeave.setAdvanceNoticeDays(0);
            sickLeave.setRequiresApproval(true);
            sickLeave.setCarryForwardLimit(3);
            sickLeave.setDescription("Paid sick leave for medical reasons");
            leavePolicyRepository.save(sickLeave);
            System.out.println("Created SICK leave policy");
        }
        
        // Casual Leave Policy
        if (!leavePolicyRepository.findByLeaveType("CASUAL").isPresent()) {
            LeavePolicy casualLeave = new LeavePolicy();
            casualLeave.setLeaveType("CASUAL");
            casualLeave.setAnnualEntitlement(10);
            casualLeave.setMaxConsecutiveDays(3);
            casualLeave.setAdvanceNoticeDays(1);
            casualLeave.setRequiresApproval(true);
            casualLeave.setCarryForwardLimit(0);
            casualLeave.setDescription("Casual leave for personal reasons");
            leavePolicyRepository.save(casualLeave);
            System.out.println("Created CASUAL leave policy");
        }
        
        // Earned Leave Policy
        if (!leavePolicyRepository.findByLeaveType("EARNED").isPresent()) {
            LeavePolicy earnedLeave = new LeavePolicy();
            earnedLeave.setLeaveType("EARNED");
            earnedLeave.setAnnualEntitlement(15);
            earnedLeave.setMaxConsecutiveDays(15);
            earnedLeave.setAdvanceNoticeDays(7);
            earnedLeave.setRequiresApproval(true);
            earnedLeave.setCarryForwardLimit(30);
            earnedLeave.setDescription("Earned/privilege leave for vacation");
            leavePolicyRepository.save(earnedLeave);
            System.out.println("Created EARNED leave policy");
        }
        
        // Maternity Leave Policy
        if (!leavePolicyRepository.findByLeaveType("MATERNITY").isPresent()) {
            LeavePolicy maternityLeave = new LeavePolicy();
            maternityLeave.setLeaveType("MATERNITY");
            maternityLeave.setAnnualEntitlement(180);
            maternityLeave.setMaxConsecutiveDays(180);
            maternityLeave.setAdvanceNoticeDays(30);
            maternityLeave.setRequiresApproval(true);
            maternityLeave.setCarryForwardLimit(0);
            maternityLeave.setDescription("Maternity leave for childbirth");
            leavePolicyRepository.save(maternityLeave);
            System.out.println("Created MATERNITY leave policy");
        }
        
        // Paternity Leave Policy
        if (!leavePolicyRepository.findByLeaveType("PATERNITY").isPresent()) {
            LeavePolicy paternityLeave = new LeavePolicy();
            paternityLeave.setLeaveType("PATERNITY");
            paternityLeave.setAnnualEntitlement(15);
            paternityLeave.setMaxConsecutiveDays(15);
            paternityLeave.setAdvanceNoticeDays(15);
            paternityLeave.setRequiresApproval(true);
            paternityLeave.setCarryForwardLimit(0);
            paternityLeave.setDescription("Paternity leave for new fathers");
            leavePolicyRepository.save(paternityLeave);
            System.out.println("Created PATERNITY leave policy");
        }
    }
    
    private void createSampleProjects() {
        // Sample Project 1
        if (!projectRepository.findByProjectCode("WEBAPP-001").isPresent()) {
            Project project1 = new Project();
            project1.setProjectCode("WEBAPP-001");
            project1.setProjectName("Company Website Redesign");
            project1.setDescription("Complete redesign of company website with modern UI/UX");
            project1.setProjectManager("pm.user");
            project1.setStartDate(LocalDate.now().minusDays(30));
            project1.setEndDate(LocalDate.now().plusDays(60));
            project1.setStatus("IN_PROGRESS");
            project1.setPriority("HIGH");
            project1.setBudget(50000.00);
            project1.setClientName("Internal");
            project1.setTechnologyStack("React, Spring Boot, MySQL");
            project1.setTeamMembers(Arrays.asList("alice.smith", "bob.johnson"));
            
            projectRepository.save(project1);
            System.out.println("Created sample project: Company Website Redesign");
        }
        
        // Sample Project 2
        if (!projectRepository.findByProjectCode("MOBILE-001").isPresent()) {
            Project project2 = new Project();
            project2.setProjectCode("MOBILE-001");
            project2.setProjectName("Employee Mobile App");
            project2.setDescription("Development of mobile application for employee self-service");
            project2.setProjectManager("pm.user");
            project2.setStartDate(LocalDate.now().minusDays(15));
            project2.setEndDate(LocalDate.now().plusDays(90));
            project2.setStatus("PLANNING");
            project2.setPriority("MEDIUM");
            project2.setBudget(75000.00);
            project2.setClientName("Internal");
            project2.setTechnologyStack("React Native, Spring Boot, MongoDB");
            project2.setTeamMembers(Arrays.asList("alice.smith"));
            
            projectRepository.save(project2);
            System.out.println("Created sample project: Employee Mobile App");
        }
    }
    
    private void createParkingSlots() {
        // Create sample parking slots for Basement Floor
        createSlotIfNotExists("B-A-01", "REGULAR", "BASEMENT", "A", "CAR", false, false);
        createSlotIfNotExists("B-A-02", "REGULAR", "BASEMENT", "A", "CAR", false, false);
        createSlotIfNotExists("B-A-03", "REGULAR", "BASEMENT", "A", "CAR", false, false);
        createSlotIfNotExists("B-A-04", "HANDICAP", "BASEMENT", "A", "CAR", false, false);
        createSlotIfNotExists("B-A-05", "ELECTRIC", "BASEMENT", "A", "ELECTRIC", false, true);
        
        createSlotIfNotExists("B-B-01", "REGULAR", "BASEMENT", "B", "CAR", true, false);
        createSlotIfNotExists("B-B-02", "REGULAR", "BASEMENT", "B", "CAR", true, false);
        createSlotIfNotExists("B-B-03", "REGULAR", "BASEMENT", "B", "CAR", true, false);
        createSlotIfNotExists("B-B-04", "EXECUTIVE", "BASEMENT", "B", "SUV", true, false);
        createSlotIfNotExists("B-B-05", "ELECTRIC", "BASEMENT", "B", "ELECTRIC", true, true);
        
        // Create sample parking slots for Ground Floor
        createSlotIfNotExists("G-A-01", "REGULAR", "GROUND", "A", "CAR", false, false);
        createSlotIfNotExists("G-A-02", "REGULAR", "GROUND", "A", "CAR", false, false);
        createSlotIfNotExists("G-A-03", "VISITOR", "GROUND", "A", "CAR", false, false);
        createSlotIfNotExists("G-A-04", "HANDICAP", "GROUND", "A", "CAR", false, false);
        createSlotIfNotExists("G-A-05", "BIKE", "GROUND", "A", "BIKE", false, false);
        
        createSlotIfNotExists("G-B-01", "REGULAR", "GROUND", "B", "CAR", true, false);
        createSlotIfNotExists("G-B-02", "REGULAR", "GROUND", "B", "CAR", true, false);
        createSlotIfNotExists("G-B-03", "EXECUTIVE", "GROUND", "B", "SUV", true, false);
        createSlotIfNotExists("G-B-04", "ELECTRIC", "GROUND", "B", "ELECTRIC", true, true);
        createSlotIfNotExists("G-B-05", "BIKE", "GROUND", "B", "BIKE", true, false);
        
        System.out.println("Created parking slots");
    }

    private void createSlotIfNotExists(String slotNumber, String slotType, String floorLevel, 
                                      String zone, String vehicleType, Boolean isCovered, Boolean hasCharging) {
        if (!parkingSlotRepository.findBySlotNumber(slotNumber).isPresent()) {
            ParkingSlot slot = new ParkingSlot();
            slot.setSlotNumber(slotNumber);
            slot.setSlotType(slotType);
            slot.setFloorLevel(floorLevel);
            slot.setZone(zone);
            slot.setVehicleType(vehicleType);
            slot.setIsCovered(isCovered);
            slot.setHasCharging(hasCharging);
            slot.setDescription(slotType + " parking slot at " + floorLevel + " floor, Zone " + zone);
            
            parkingSlotRepository.save(slot);
        }
    }

    private void createSampleParkingAllocations() {
        // Assign parking slot to project manager
        assignParkingIfNotExists("pm.user", "B-B-04", "KA01AB1234", "Toyota Fortuner", "SUV", "White");
        
        // Assign parking slot to admin
        assignParkingIfNotExists("admin", "G-B-03", "KA01CD5678", "Honda City", "CAR", "Silver");
        
        System.out.println("Created sample parking allocations");
    }

    private void assignParkingIfNotExists(String username, String slotNumber, String vehicleNumber, 
                                         String vehicleModel, String vehicleType, String vehicleColor) {
        if (!parkingAllocationRepository.findByUsernameAndStatus(username, "ACTIVE").isPresent()) {
            ParkingAllocation allocation = new ParkingAllocation();
            allocation.setUsername(username);
            allocation.setSlotNumber(slotNumber);
            allocation.setVehicleNumber(vehicleNumber);
            allocation.setVehicleType(vehicleType);
            allocation.setVehicleModel(vehicleModel);
            allocation.setVehicleColor(vehicleColor);
            allocation.setValidFrom(java.time.LocalDate.now());
            allocation.setApprovedBy("system");
            allocation.setApprovalDate(java.time.LocalDate.now());
            allocation.setNotes("Initial allocation");
            
            parkingAllocationRepository.save(allocation);
            
            // Update slot status
            parkingSlotRepository.findBySlotNumber(slotNumber).ifPresent(slot -> {
                slot.setStatus("OCCUPIED");
                parkingSlotRepository.save(slot);
            });
        }
    }
}