package com.staffmanagement.repository;

import com.staffmanagement.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    
	// Add this method to SalaryRepository interface
	@Modifying
	@Query(value = "ALTER TABLE SALARIES ALTER COLUMN ID RESTART WITH 1", nativeQuery = true)
	void resetSalarySequence();
    // Find salary by username and month
    Optional<Salary> findByUsernameAndSalaryMonth(String username, YearMonth salaryMonth);
    
    // Find all salaries for a user
    List<Salary> findByUsernameOrderBySalaryMonthDesc(String username);
    
    // Find all salaries for a specific month
    List<Salary> findBySalaryMonthOrderByUsername(YearMonth salaryMonth);
    
    // Find salaries by payment status
    List<Salary> findByPaymentStatusOrderBySalaryMonthDesc(String paymentStatus);
    
    // Check if salary exists for user and month
    boolean existsByUsernameAndSalaryMonth(String username, YearMonth salaryMonth);
    
    // Get latest salary for a user - FIXED METHOD
    @Query("SELECT s FROM Salary s WHERE s.username = :username ORDER BY s.salaryMonth DESC")
    List<Salary> findLatestSalaryByUsername(@Param("username") String username);
    
    // Alternative method to get single latest salary
    default Optional<Salary> findMostRecentSalaryByUsername(String username) {
        List<Salary> salaries = findLatestSalaryByUsername(username);
        return salaries.stream().findFirst();
    }
    
    // Get salary summary for dashboard
    @Query("SELECT s.salaryMonth, COUNT(s), SUM(s.netSalary), AVG(s.netSalary) FROM Salary s WHERE s.salaryMonth = :month GROUP BY s.salaryMonth")
    Object[] findSalarySummaryByMonth(@Param("month") YearMonth month);
    
    // Find salaries by username and date range
    @Query("SELECT s FROM Salary s WHERE s.username = :username AND s.salaryMonth BETWEEN :startMonth AND :endMonth ORDER BY s.salaryMonth DESC")
    List<Salary> findByUsernameAndSalaryMonthBetween(@Param("username") String username, 
                                                   @Param("startMonth") YearMonth startMonth, 
                                                   @Param("endMonth") YearMonth endMonth);
    
    // Calculate total salary paid by month
    @Query("SELECT s.salaryMonth, SUM(s.netSalary) FROM Salary s WHERE s.paymentStatus = 'PAID' GROUP BY s.salaryMonth ORDER BY s.salaryMonth DESC")
    List<Object[]> findTotalSalaryPaidByMonth();
}