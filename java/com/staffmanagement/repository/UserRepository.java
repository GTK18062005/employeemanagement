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
    
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    
    // Add this method
    boolean existsByEmail(String email);
    
    // Fix the findByUsernameAndPassword method
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, 
                                            @Param("password") String password);
    
    // Other existing methods...
    List<User> findByRole(String role);
    List<User> findByDepartment(String department);
    List<User> findByDesignation(String designation);
    List<User> findByRoleAndDepartment(String role, String department);
    
    // Reset sequence for H2 database
    @Modifying
    @Query(value = "ALTER TABLE users ALTER COLUMN id RESTART WITH 1", nativeQuery = true)
    void resetUserSequence();
    
    // OR use this query method instead of the above boolean method
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email")
    boolean existsByEmailQuery(@Param("email") String email);
}