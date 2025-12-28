package com.staffmanagement.repository;

import com.staffmanagement.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    
    List<Leave> findByUsernameOrderByAppliedDateDesc(String username);
    
    List<Leave> findByStatusOrderByAppliedDateDesc(String status);
    
    List<Leave> findByLeaveTypeAndStatus(String leaveType, String status);
    
    @Query("SELECT l FROM Leave l WHERE l.startDate BETWEEN :startDate AND :endDate OR l.endDate BETWEEN :startDate AND :endDate")
    List<Leave> findLeavesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT l FROM Leave l WHERE l.username = :username AND l.status = 'APPROVED' AND " +
           "(l.startDate BETWEEN :startDate AND :endDate OR l.endDate BETWEEN :startDate AND :endDate)")
    List<Leave> findApprovedLeavesByUserAndDateRange(@Param("username") String username, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    long countByUsernameAndStatus(String username, String status);
    
    long countByUsernameAndLeaveTypeAndStatus(String username, String leaveType, String status);
}