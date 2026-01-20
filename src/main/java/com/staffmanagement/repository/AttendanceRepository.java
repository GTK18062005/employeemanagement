package com.staffmanagement.repository;

import com.staffmanagement.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    // Find attendance by username and date
    Optional<Attendance> findByUsernameAndAttendanceDate(String username, LocalDate attendanceDate);
    
    List<Attendance> findByUsernameOrderByAttendanceDateDesc(String username);
    
    @Query("SELECT a FROM Attendance a WHERE a.username = :username AND a.attendanceDate = CURRENT_DATE")
    Optional<Attendance> findTodayAttendance(@Param("username") String username);
    
    @Query("SELECT a FROM Attendance a WHERE a.username = :username AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month ORDER BY a.attendanceDate DESC")
    List<Attendance> findMonthlyAttendance(@Param("username") String username, @Param("year") int year, @Param("month") int month);
    
    List<Attendance> findByAttendanceDateOrderByUsername(LocalDate attendanceDate);
    
    // Reset sequence for H2 database
    @Modifying
    @Query(value = "ALTER TABLE ATTENDANCE ALTER COLUMN ID RESTART WITH 1", nativeQuery = true)
    void resetAttendanceSequence();
    
    // Count methods for statistics
    long countByUsername(String username);
    long countByUsernameAndStatus(String username, String status);
}