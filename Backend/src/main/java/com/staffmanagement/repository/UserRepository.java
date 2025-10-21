package com.staffmanagement.repository;

import com.staffmanagement.model.Salary;
import com.staffmanagement.model.SalaryResponse;
import com.staffmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ===== BASIC CRUD METHODS =====
    
    // Find user by username (returns Optional)
    Optional<User> findByUsername(String username);
    
    // Find user by username and password
    Optional<User> findByUsernameAndPassword(String username, String password);
    
    // Custom query to find user profile by username
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findUserProfileByUsername(@Param("username") String username);
    
    // Find user by email
    User findByEmail(String email);
    
    // Find all users by role
    List<User> findByRole(String role);
    
    // Find all users by department
    List<User> findByDepartment(String department);
    
    // Find all users by designation
    List<User> findByDesignation(String designation);
    
    // Find users by role and department
    List<User> findByRoleAndDepartment(String role, String department);
    
    // ===== EXISTENCE CHECK METHODS =====
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // ===== SEARCH METHODS =====
    
    // Find users by name containing (search functionality)
    List<User> findByNameContainingIgnoreCase(String name);
    
    // Find users by username containing
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    // Search users by multiple criteria
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:department IS NULL OR u.department = :department) AND " +
           "(:role IS NULL OR u.role = :role)")
    List<User> searchUsers(@Param("name") String name, 
                          @Param("department") String department, 
                          @Param("role") String role);
    
    // ===== COUNT METHODS =====
    
    // Count users by role
    long countByRole(String role);
    
    // Count users by department
    long countByDepartment(String department);
    
    // Get total user count with role breakdown
    @Query("SELECT COUNT(u), " +
           "SUM(CASE WHEN u.role = 'ADMIN' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.role = 'STAFF' THEN 1 ELSE 0 END) " +
           "FROM User u")
    Object[] getUserStatistics();
    
    // ===== QUERY METHODS =====
    
    // Get all usernames
    @Query("SELECT u.username FROM User u")
    List<String> findAllUsernames();
    
    // Get users with specific roles
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findByRoles(@Param("roles") List<String> roles);
    
    // Find users with bank account information
    @Query("SELECT u FROM User u WHERE u.bankAccountNumber IS NOT NULL AND u.bankAccountNumber != ''")
    List<User> findUsersWithBankAccount();
    
    // Find users without bank account
    @Query("SELECT u FROM User u WHERE u.bankAccountNumber IS NULL OR u.bankAccountNumber = ''")
    List<User> findUsersWithoutBankAccount();
    
    // Find users by multiple usernames
    @Query("SELECT u FROM User u WHERE u.username IN :usernames")
    List<User> findByUsernames(@Param("usernames") List<String> usernames);
    
    // Find active users
    @Query("SELECT u FROM User u WHERE u.role != 'INACTIVE'")
    List<User> findActiveUsers();
    
    // ===== AGGREGATION METHODS =====
    
    // Get user count by department
    @Query("SELECT u.department, COUNT(u) FROM User u GROUP BY u.department")
    List<Object[]> countUsersByDepartment();
    
    // Get user count by role and department
    @Query("SELECT u.department, u.role, COUNT(u) FROM User u GROUP BY u.department, u.role")
    List<Object[]> countUsersByDepartmentAndRole();
    
    // ===== UPDATE METHODS =====
    
    // Update user password using custom query
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePassword(@Param("username") String username, @Param("password") String password);
    
    // Update user profile information
    @Query("UPDATE User u SET u.name = :name, u.email = :email, u.phone = :phone, " +
           "u.department = :department, u.designation = :designation, " +
           "u.address = :address, u.emergencyContact = :emergencyContact, " +
           "u.bankAccountNumber = :bankAccountNumber, u.panNumber = :panNumber " +
           "WHERE u.username = :username")
    void updateUserProfile(@Param("username") String username,
                         @Param("name") String name,
                         @Param("email") String email,
                         @Param("phone") String phone,
                         @Param("department") String department,
                         @Param("designation") String designation,
                         @Param("address") String address,
                         @Param("emergencyContact") String emergencyContact,
                         @Param("bankAccountNumber") String bankAccountNumber,
                         @Param("panNumber") String panNumber);
    
    // ===== SPECIFIC PROFILE METHODS =====
    
    // Get user profile with limited fields (for security)
    @Query("SELECT NEW com.staffmanagement.model.User(u.id, u.username, u.name, u.email, u.phone, " +
           "u.department, u.designation, u.role, u.address, u.emergencyContact, " +
           "u.bankAccountNumber, u.panNumber, u.createdAt, u.updatedAt) " +
           "FROM User u WHERE u.username = :username")
    Optional<User> findBasicProfileByUsername(@Param("username") String username);
    
    // Get user profile without sensitive information
    @Query("SELECT u.id, u.username, u.name, u.email, u.phone, u.department, " +
           "u.designation, u.role, u.address, u.emergencyContact, u.createdAt " +
           "FROM User u WHERE u.username = :username")
    Optional<Object[]> findSafeProfileByUsername(@Param("username") String username);
    public default SalaryResponse convertToResponse(Salary salary) {
        UserRepository userRepository = null;
		Optional<User> userOptional = userRepository.findByUsername(salary.getUsername());
        User user = userOptional.orElse(null); // Returns null if not found
        
        return convertToResponse(salary);
    }
    // ===== BULK OPERATIONS =====
    
    // Update department for multiple users
    @Query("UPDATE User u SET u.department = :department WHERE u.username IN :usernames")
    void updateDepartmentForUsers(@Param("usernames") List<String> usernames, 
                                 @Param("department") String department);
    
    // Update role for multiple users
    @Query("UPDATE User u SET u.role = :role WHERE u.username IN :usernames")
    void updateRoleForUsers(@Param("usernames") List<String> usernames, 
                           @Param("role") String role);
    
    // ===== ADVANCED SEARCH METHODS =====
    
    // Search users with pagination support
    @Query("SELECT u FROM User u WHERE " +
           "(:search IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.department) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(u.designation) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:department IS NULL OR u.department = :department) " +
           "AND (:role IS NULL OR u.role = :role)")
    List<User> advancedSearchUsers(@Param("search") String search,
                                  @Param("department") String department,
                                  @Param("role") String role);
    
    // Find users by creation date range
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate,
                                     @Param("endDate") java.time.LocalDateTime endDate);
    
    // ===== VALIDATION METHODS =====
 // Add this method to UserRepository interface
    @Modifying
    @Query(value = "ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1", nativeQuery = true)
    void resetUserSequence();
    
    // Check if username is available (for new registrations)
    @Query("SELECT CASE WHEN COUNT(u) = 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean isUsernameAvailable(@Param("username") String username);
    
    // Check if email is available (for new registrations)
    @Query("SELECT CASE WHEN COUNT(u) = 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean isEmailAvailable(@Param("email") String email);
}