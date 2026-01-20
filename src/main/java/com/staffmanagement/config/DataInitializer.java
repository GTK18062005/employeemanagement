package com.staffmanagement.config;

import com.staffmanagement.model.*;
import com.staffmanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        logger.info("Starting application data initialization...");
        try {
            clearExistingData();
            createLeavePolicies();
            createSampleUsers();
            createSampleSalaries();
            createSampleProjects();
            createParkingSlots();
            createSampleParkingAllocations();
            logger.info("Data initialization completed successfully!");
        } catch (Exception e) {
            logger.error("Data initialization failed", e);
        }
    }

    private void clearExistingData() {
        logger.info("Clearing existing data...");
        try {
            // Delete in correct order to avoid foreign key constraints
            parkingAllocationRepository.deleteAll();
            parkingRequestRepository.deleteAll();
            attendanceRepository.deleteAll();
            salaryRepository.deleteAll();
            
            if (projectRepository != null) {
                projectRepository.deleteAll();
            }
            
            parkingSlotRepository.deleteAll();
            leavePolicyRepository.deleteAll();
            userRepository.deleteAll();

            resetSequences();
            logger.info("Successfully cleared existing data");
        } catch (Exception e) {
            logger.warn("Error clearing existing data: {}", e.getMessage());
        }
    }

    private void resetSequences() {
        try {
            userRepository.resetUserSequence();
            attendanceRepository.resetAttendanceSequence();
            salaryRepository.resetSalarySequence();
            logger.debug("Database sequences reset successfully");
        } catch (Exception e) {
            logger.warn("Could not reset sequences: {}", e.getMessage());
        }
    }

    private void createSampleUsers() {
        logger.info("Creating sample users...");
        
        createAdminUser();
        createProjectManagerUser();
        createStaffUsers();
        
        logger.info("Sample users created successfully");
    }

    private User createAdminUser() {
        if (userRepository.existsByUsername("admin")) {
            logger.info("Admin user already exists");
            return userRepository.findByUsername("admin").get();
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setName("System Administrator");
        admin.setEmail("admin@company.com");
        admin.setRole(UserRole.ADMIN);
        admin.setDepartment("Administration");
        admin.setDesignation("System Administrator");
        admin.setPhone("+919876543210");
        admin.setBankAccountNumber("ACC00123456");
        admin.setIsActive(true);
        admin.setJoiningDate(LocalDate.now().minusYears(5));

        userRepository.save(admin);
        logger.info("Created admin user: {}", admin.getName());
        return admin;
    }

    private void createProjectManagerUser() {
        if (userRepository.existsByUsername("pm.user")) {
            return;
        }

        User pm = new User();
        pm.setUsername("pm.user");
        pm.setPassword(passwordEncoder.encode("pm123"));
        pm.setName("Sarah Johnson");
        pm.setEmail("sarah.johnson@company.com");
        pm.setPhone("+91-9876543211");
        pm.setDepartment("IT");
        pm.setDesignation("Project Manager");
        pm.setRole(UserRole.PROJECT_MANAGER);
        pm.setAddress("789 Oakwood Street, Chennai");
        pm.setEmergencyContact("+919123456789");
        pm.setBankAccountNumber("ACC00998877");
        pm.setIsActive(true);
        pm.setJoiningDate(LocalDate.now().minusYears(1));

        userRepository.save(pm);
        logger.info("Created project manager: {}", pm.getName());
        if (userRepository.existsByUsername("GTK")) {
            return;
        }

        User p = new User();
        p.setUsername("GTK");
        p.setPassword(passwordEncoder.encode("tharun"));
        p.setName("GTK");
        p.setEmail("tk123@gmail.com");
        p.setPhone("+91-9876543210");
        p.setDepartment("CSE");
        p.setDesignation("Project Manager");
        p.setRole(UserRole.PROJECT_MANAGER);
        p.setAddress("789 Oakwood Street, Chennai");
        p.setEmergencyContact("+919123456722");
        p.setBankAccountNumber("ACC0998877");
        p.setIsActive(true);
        p.setJoiningDate(LocalDate.now().minusYears(3));

        userRepository.save(p);
        logger.info("Created project manager: {}", p.getName());
    }

    private void createStaffUsers() {
        List<User> staffUsers = Arrays.asList(
            createStaffUser("john.doe", "John Doe", "HR", "HR Manager", "john.doe@company.com", "+91-9876543212"),
            createStaffUser("alice.smith", "Alice Smith", "IT", "Software Developer", "alice.smith@company.com", "+91-9876543213"),
            createStaffUser("bob.johnson", "Bob Johnson", "IT", "Senior Developer", "bob.johnson@company.com", "+91-9876543214"),
            createStaffUser("carol.wilson", "Carol Wilson", "Finance", "Accountant", "carol.wilson@company.com", "+91-9876543215"),
            createStaffUser("michale", "michale", "HR", "HR Manager", "john.de@company.com", "+91-9876543212"),
            createStaffUser("rajesh", "raj", "IT", "Software Developer", "joh.doe@company.com", "+91-9876543212"),
            createStaffUser("ram", "ram", "HR", "HR Manager", "jon.doe@company.com", "+91-9876543212")
        );
        
        staffUsers.forEach(user -> logger.info("Created staff user: {}", user.getName()));
    }

    private User createStaffUser(String username, String name, String department, String designation, 
                               String email, String phone) {
        if (userRepository.existsByUsername(username)) {
            return userRepository.findByUsername(username).get();
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(username + "123"));
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setDepartment(department);
        user.setDesignation(designation);
        user.setRole(UserRole.STAFF);
        user.setBankAccountNumber("ACC00" + (100000 + userRepository.count()));
        user.setPanNumber("ABCDE" + (1000 + userRepository.count()) + "K");
        user.setIsActive(true);
        user.setJoiningDate(LocalDate.now().minusMonths((int)(Math.random() * 24)));

        return userRepository.save(user);
    }

    private void createSampleSalaries() {
        logger.info("Creating sample salaries...");
        YearMonth currentMonth = YearMonth.now();
        YearMonth lastMonth = currentMonth.minusMonths(1);
        YearMonth twoMonthsAgo = currentMonth.minusMonths(2);

        List<String> usernames = Arrays.asList("pm.user", "alice.smith", "bob.johnson", "john.doe");
        
        for (String username : usernames) {
            createSalaryForUser(username, lastMonth);
            createSalaryForUser(username, twoMonthsAgo);
        }
        
        logger.info("Sample salaries created");
    }

    private void createSalaryForUser(String username, YearMonth salaryMonth) {
        if (salaryRepository.existsByUsernameAndSalaryMonth(username, salaryMonth)) {
            return;
        }

        Salary salary = new Salary();
        salary.setUsername(username);
        salary.setSalaryMonth(salaryMonth);
        
        // Generate realistic salary components
        BigDecimal basicSalary = generateBasicSalary(username);
        salary.setBasicSalary(basicSalary);
        salary.setHouseRentAllowance(basicSalary.multiply(new BigDecimal("0.40")));
        salary.setTravelAllowance(new BigDecimal("1600.00"));
        salary.setMedicalAllowance(new BigDecimal("1250.00"));
        salary.setBonus(new BigDecimal("3000.00"));
        
        double overtimeHours = Math.random() * 10;
        salary.setOvertimeHours(overtimeHours);
        salary.setOvertimeRate(new BigDecimal("200.00"));
        salary.setOvertimePay(new BigDecimal("200.00").multiply(BigDecimal.valueOf(overtimeHours)));
        
        salary.setTaxDeduction(calculateTax(salary));
        salary.setProvidentFund(basicSalary.multiply(new BigDecimal("0.12")));
        salary.setOtherDeductions(new BigDecimal("500.00"));
        
        salary.setGrossSalary(calculateGrossSalary(salary));
        salary.setNetSalary(salary.getGrossSalary().subtract(salary.getTaxDeduction())
                                        .subtract(salary.getProvidentFund())
                                        .subtract(salary.getOtherDeductions()));
        salary.setPaymentStatus(Math.random() > 0.2 ? "PAID" : "PENDING");
        salary.setPaymentDate(salary.getPaymentStatus().equals("PAID") ? 
                             LocalDate.now().minusDays((int)(Math.random() * 10)) : null);

        User user = userRepository.findByUsername(username).orElseThrow();
        salary.setBankAccountNumber(user.getBankAccountNumber());

        salaryRepository.save(salary);
    }

    private BigDecimal generateBasicSalary(String username) {
        return switch (username) {
            case "pm.user" -> new BigDecimal("75000.00");
            case "john.doe" -> new BigDecimal("65000.00");
            case "bob.johnson" -> new BigDecimal("70000.00");
            default -> new BigDecimal("55000.00");
        };
    }

    private BigDecimal calculateTax(Salary salary) {
        BigDecimal gross = salary.getBasicSalary()
                .add(salary.getHouseRentAllowance())
                .add(salary.getTravelAllowance())
                .add(salary.getMedicalAllowance())
                .add(salary.getBonus())
                .add(salary.getOvertimePay());
        return gross.multiply(new BigDecimal("0.08"));
    }

    private BigDecimal calculateGrossSalary(Salary salary) {
        return salary.getBasicSalary()
                .add(salary.getHouseRentAllowance())
                .add(salary.getTravelAllowance())
                .add(salary.getMedicalAllowance())
                .add(salary.getBonus())
                .add(salary.getOvertimePay());
    }

    private void createLeavePolicies() {
        logger.info("Creating leave policies...");
        
        // Remove existing if any
        leavePolicyRepository.deleteAll();
        
        List<LeavePolicy> policies = Arrays.asList(
            createLeavePolicy("SICK", 12, 5, 0, true, 3, "Paid sick leave for medical reasons"),
            createLeavePolicy("CASUAL", 10, 3, 1, true, 0, "Casual leave for personal reasons"),
            createLeavePolicy("EARNED", 15, 15, 7, true, 30, "Earned/privilege leave for vacation"),
            createLeavePolicy("MATERNITY", 180, 180, 30, true, 0, "Maternity leave for childbirth"),
            createLeavePolicy("PATERNITY", 15, 15, 15, true, 0, "Paternity leave for new fathers")
        );
        
        leavePolicyRepository.saveAll(policies);
        policies.forEach(policy -> logger.info("Created {} leave policy", policy.getLeaveType()));
    }

    private LeavePolicy createLeavePolicy(String type, int entitlement, int maxConsecutive, 
                                        int noticeDays, boolean requiresApproval, 
                                        int carryForward, String description) {
        LeavePolicy policy = new LeavePolicy();
        policy.setLeaveType(type);
        policy.setAnnualEntitlement(entitlement);
        policy.setMaxConsecutiveDays(maxConsecutive);
        policy.setAdvanceNoticeDays(noticeDays);
        policy.setRequiresApproval(requiresApproval);
        policy.setCarryForwardLimit(carryForward);
        policy.setDescription(description);
        policy.setIsActive(true);
        return policy;
    }

    private void createSampleProjects() {
        if (projectRepository == null) {
            logger.warn("ProjectRepository not available, skipping projects");
            return;
        }

        logger.info("Creating sample projects...");
        List<Project> projects = Arrays.asList(
            createProject("WEBAPP-001", "Company Website Redesign", 
                         "Complete redesign with modern UI/UX", "IN_PROGRESS", "HIGH", 50000.0),
            createProject("MOBILE-001", "Employee Mobile App", 
                         "Mobile app for employee self-service", "PLANNING", "MEDIUM", 75000.0),
            createProject("API-001", "REST API Backend", 
                         "Centralized REST API for all applications", "IN_PROGRESS", "HIGH", 40000.0)
        );
        
        projectRepository.saveAll(projects);
        projects.forEach(p -> logger.info("Created project: {}", p.getProjectName()));
    }

    private Project createProject(String code, String name, String description, 
                                String status, String priority, double budget) {
        Project project = new Project();
        project.setProjectCode(code);
        project.setProjectName(name);
        project.setDescription(description);
        project.setProjectManager("pm.user");
        project.setStartDate(LocalDate.now().minusDays((int)(Math.random() * 60)));
        project.setEndDate(LocalDate.now().plusDays((int)(Math.random() * 120)));
        project.setStatus(status);
        project.setPriority(priority);
        project.setBudget(budget);
        project.setClientName("Internal");
        project.setTechnologyStack("Java, Spring Boot, React");
        project.setTeamMembers(Arrays.asList("alice.smith", "bob.johnson"));
        project.setCreatedDate(LocalDate.now());
        project.setLastUpdated(LocalDate.now());

        return project;
    }

    private void createParkingSlots() {
        logger.info("Creating parking slots...");
        List<Object[]> slots = Arrays.asList(
            new Object[]{"B-A-01", "REGULAR", "BASEMENT", "A", "CAR", false, false},
            new Object[]{"B-A-02", "REGULAR", "BASEMENT", "A", "CAR", false, false},
            new Object[]{"B-A-04", "HANDICAP", "BASEMENT", "A", "CAR", false, false},
            new Object[]{"B-A-05", "ELECTRIC", "BASEMENT", "A", "ELECTRIC", false, true},
            new Object[]{"G-A-01", "REGULAR", "GROUND", "A", "CAR", false, false},
            new Object[]{"G-A-03", "VISITOR", "GROUND", "A", "CAR", false, false},
            new Object[]{"G-A-05", "BIKE", "GROUND", "A", "BIKE", false, false},
            new Object[]{"B-B-04", "EXECUTIVE", "BASEMENT", "B", "SUV", true, false},
            new Object[]{"G-B-03", "EXECUTIVE", "GROUND", "B", "SUV", true, false}
        );

        for (Object[] config : slots) {
            String slotNumber = (String) config[0];
            Optional<ParkingSlot> existing = parkingSlotRepository.findBySlotNumber(slotNumber);
            if (existing.isEmpty()) {
                ParkingSlot slot = new ParkingSlot();
                slot.setSlotNumber(slotNumber);
                slot.setSlotType((String) config[1]);
                slot.setFloorLevel((String) config[2]);
                slot.setZone((String) config[3]);
                slot.setVehicleType((String) config[4]);
                slot.setIsCovered((Boolean) config[5]);
                slot.setHasCharging((Boolean) config[6]);
                slot.setStatus("AVAILABLE");
                slot.setDescription(slot.getSlotType() + " slot at " + slot.getFloorLevel() + " floor, Zone " + slot.getZone());
                parkingSlotRepository.save(slot);
            }
        }
        
        logger.info("Created parking slots");
    }

    private void createSampleParkingAllocations() {
        logger.info("Creating sample parking allocations...");
        List<Object[]> allocations = Arrays.asList(
            new Object[]{"pm.user", "B-B-04", "KA01AB1234", "Toyota Fortuner", "SUV", "White"},
            new Object[]{"admin", "G-B-03", "KA01CD5678", "Honda City", "CAR", "Silver"},
            new Object[]{"john.doe", "B-A-01", "KA01EF9012", "Hyundai Creta", "SUV", "Blue"}
        );

        for (Object[] config : allocations) {
            String username = (String) config[0];
            String slotNumber = (String) config[1];
            String vehicleNumber = (String) config[2];
            String vehicleModel = (String) config[3];
            String vehicleType = (String) config[4];
            String vehicleColor = (String) config[5];
            
            Optional<ParkingAllocation> existing = parkingAllocationRepository.findByUsernameAndStatus(username, "ACTIVE");
            if (existing.isEmpty()) {
                ParkingAllocation allocation = new ParkingAllocation();
                allocation.setUsername(username);
                allocation.setSlotNumber(slotNumber);
                allocation.setVehicleNumber(vehicleNumber);
                allocation.setVehicleModel(vehicleModel);
                allocation.setVehicleType(vehicleType);
                allocation.setVehicleColor(vehicleColor);
                allocation.setValidFrom(LocalDate.now());
                allocation.setValidUntil(LocalDate.now().plusMonths(12));
                allocation.setStatus("ACTIVE");
                allocation.setApprovedBy("system");
                allocation.setApprovalDate(LocalDate.now());
                allocation.setNotes("Initial system allocation");
                parkingAllocationRepository.save(allocation);

                // Update slot status
                parkingSlotRepository.findBySlotNumber(slotNumber).ifPresent(slot -> {
                    slot.setStatus("OCCUPIED");
                    parkingSlotRepository.save(slot);
                });
            }
        }
        
        logger.info("Created parking allocations");
    }
}