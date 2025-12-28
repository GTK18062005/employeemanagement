package com.staffmanagement.repository;

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
    
    // Basic CRUD methods
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPassword(String username, String password);
    
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findUserProfileByUsername(@Param("username") String username);
    
    User findByEmail(String email);
    List<User> findByRole(String role);
    List<User> findByDepartment(String department);
    List<User> findByDesignation(String designation);
    List<User> findByRoleAndDepartment(String role, String department);
    
    // Existence check methods
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Search methods
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    @Query("SELECT u FROM User u WHERE " +
           "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:department IS NULL OR u.department = :department) AND " +
           "(:role IS NULL OR u.role = :role)")
    List<User> searchUsers(@Param("name") String name, 
                          @Param("department") String department, 
                          @Param("role") String role);
    
    // Count methods
    long countByRole(String role);
    long countByDepartment(String department);
    
    @Query("SELECT COUNT(u), " +
           "SUM(CASE WHEN u.role = 'ADMIN' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.role = 'STAFF' THEN 1 ELSE 0 END) " +
           "FROM User u")
    Object[] getUserStatistics();
    
    // Query methods
    @Query("SELECT u.username FROM User u")
    List<String> findAllUsernames();
    
    @Query("SELECT u FROM User u WHERE u.role IN :roles")
    List<User> findByRoles(@Param("roles") List<String> roles);
    
    // Reset sequence for H2 database
    @Modifying
    @Query(value = "ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1", nativeQuery = true)
    void resetUserSequence();
    
    // Check if username is available
    @Query("SELECT CASE WHEN COUNT(u) = 0 THEN true ELSE false END FROM User u WHERE u.username = :username")
    boolean isUsernameAvailable(@Param("username") String username);
    
    // Check if email is available
    @Query("SELECT CASE WHEN COUNT(u) = 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean isEmailAvailable(@Param("email") String email);
}