package com.staffmanagement.service;

import com.staffmanagement.model.Attendance;
import com.staffmanagement.model.AttendanceRequest;
import com.staffmanagement.model.AttendanceResponse;
import com.staffmanagement.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AttendanceService {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    public AttendanceResponse markCheckIn(String username) {
        LocalDate today = LocalDate.now();
        
        try {
            // Check if attendance already exists for today
            Optional<Attendance> existingAttendance = attendanceRepository.findByUsernameAndAttendanceDate(username, today);
            
            if (existingAttendance.isPresent()) {
                Attendance attendance = existingAttendance.get();
                if (attendance.getCheckInTime() != null) {
                    throw new RuntimeException("You have already checked in today at " + attendance.getCheckInTime());
                }
                // Update existing record
                attendance.setCheckInTime(LocalTime.now());
                attendance.setStatus("PRESENT");
                Attendance savedAttendance = saveAttendanceSafely(attendance);
                return convertToResponse(savedAttendance);
            } else {
                // Create new attendance record
                Attendance attendance = new Attendance();
                attendance.setUsername(username);
                attendance.setAttendanceDate(today);
                attendance.setCheckInTime(LocalTime.now());
                attendance.setStatus("PRESENT");
                
                Attendance savedAttendance = saveAttendanceSafely(attendance);
                return convertToResponse(savedAttendance);
            }
        } catch (DataIntegrityViolationException e) {
            // Handle unique constraint violation
            throw new RuntimeException("Attendance record already exists for today. Please refresh and try again.");
        } catch (Exception e) {
            throw new RuntimeException("Error marking check-in: " + e.getMessage());
        }
    }
    
    public AttendanceResponse markCheckOut(String username) {
        LocalDate today = LocalDate.now();
        
        try {
            Optional<Attendance> attendanceOpt = attendanceRepository.findByUsernameAndAttendanceDate(username, today);
            
            if (attendanceOpt.isEmpty()) {
                throw new RuntimeException("You haven't checked in today. Please check in first.");
            }
            
            Attendance attendance = attendanceOpt.get();
            
            if (attendance.getCheckOutTime() != null) {
                throw new RuntimeException("You have already checked out today at " + attendance.getCheckOutTime());
            }
            
            if (attendance.getCheckInTime() == null) {
                throw new RuntimeException("Check-in time not found. Please check in first.");
            }
            
            attendance.setCheckOutTime(LocalTime.now());
            
            // Calculate working hours
            long minutes = ChronoUnit.MINUTES.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
            double hours = minutes / 60.0;
            attendance.setWorkingHours(Math.round(hours * 100.0) / 100.0);
            
            Attendance savedAttendance = saveAttendanceSafely(attendance);
            return convertToResponse(savedAttendance);
            
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error updating attendance record. Please try again.");
        } catch (Exception e) {
            throw new RuntimeException("Error marking check-out: " + e.getMessage());
        }
    }
    
    public AttendanceResponse markManualAttendance(AttendanceRequest request) {
        LocalDate today = LocalDate.now();
        
        // Validate required fields
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        
        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new RuntimeException("Status is required");
        }
        
        try {
            // Check if attendance already exists for today
            Optional<Attendance> existingAttendance = attendanceRepository.findByUsernameAndAttendanceDate(request.getUsername(), today);
            
            Attendance attendance;
            if (existingAttendance.isPresent()) {
                attendance = existingAttendance.get();
            } else {
                attendance = new Attendance();
                attendance.setUsername(request.getUsername());
                attendance.setAttendanceDate(today);
            }
            
            // Update fields
            if (request.getCheckInTime() != null) {
                attendance.setCheckInTime(request.getCheckInTime());
            }
            if (request.getCheckOutTime() != null) {
                attendance.setCheckOutTime(request.getCheckOutTime());
            }
            if (request.getStatus() != null) {
                attendance.setStatus(request.getStatus());
            }
            if (request.getNotes() != null) {
                attendance.setNotes(request.getNotes());
            }
            
            // Calculate working hours if both check-in and check-out are provided
            if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
                long minutes = ChronoUnit.MINUTES.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
                double hours = minutes / 60.0;
                attendance.setWorkingHours(Math.round(hours * 100.0) / 100.0);
            }
            
            Attendance savedAttendance = saveAttendanceSafely(attendance);
            return convertToResponse(savedAttendance);
            
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Attendance record conflict. Please try again.");
        } catch (Exception e) {
            throw new RuntimeException("Error recording manual attendance: " + e.getMessage());
        }
    }
    
    /**
     * Safe method to save attendance with retry logic for ID conflicts
     */
    private Attendance saveAttendanceSafely(Attendance attendance) {
        try {
            return attendanceRepository.save(attendance);
        } catch (DataIntegrityViolationException e) {
            // If there's a primary key violation, try to find existing record and update it
            if (e.getMessage().contains("PRIMARY KEY")) {
                Optional<Attendance> existing = attendanceRepository.findByUsernameAndAttendanceDate(
                    attendance.getUsername(), attendance.getAttendanceDate());
                if (existing.isPresent()) {
                    Attendance existingAttendance = existing.get();
                    // Update existing record with new values
                    if (attendance.getCheckInTime() != null) {
                        existingAttendance.setCheckInTime(attendance.getCheckInTime());
                    }
                    if (attendance.getCheckOutTime() != null) {
                        existingAttendance.setCheckOutTime(attendance.getCheckOutTime());
                    }
                    if (attendance.getStatus() != null) {
                        existingAttendance.setStatus(attendance.getStatus());
                    }
                    if (attendance.getWorkingHours() != null) {
                        existingAttendance.setWorkingHours(attendance.getWorkingHours());
                    }
                    if (attendance.getNotes() != null) {
                        existingAttendance.setNotes(attendance.getNotes());
                    }
                    return attendanceRepository.save(existingAttendance);
                }
            }
            throw e;
        }
    }
    
    public List<AttendanceResponse> getUserAttendance(String username) {
        List<Attendance> attendances = attendanceRepository.findByUsernameOrderByAttendanceDateDesc(username);
        return attendances.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<AttendanceResponse> getUserAttendanceByMonth(String username, int year, int month) {
        List<Attendance> attendances = attendanceRepository.findMonthlyAttendance(username, year, month);
        return attendances.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public AttendanceResponse getTodayAttendance(String username) {
        Optional<Attendance> attendance = attendanceRepository.findByUsernameAndAttendanceDate(username, LocalDate.now());
        return attendance.map(this::convertToResponse).orElse(null);
    }
    
    public List<AttendanceResponse> getAllAttendanceByDate(LocalDate date) {
        List<Attendance> attendances = attendanceRepository.findByAttendanceDateOrderByUsername(date);
        return attendances.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public AttendanceStats getAttendanceStats(String username) {
        List<Attendance> recentAttendances = attendanceRepository.findByUsernameOrderByAttendanceDateDesc(username);
        
        long totalDays = recentAttendances.size();
        long presentDays = recentAttendances.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()))
                .count();
        long absentDays = recentAttendances.stream()
                .filter(a -> "ABSENT".equals(a.getStatus()))
                .count();
        long halfDays = recentAttendances.stream()
                .filter(a -> "HALF_DAY".equals(a.getStatus()))
                .count();
        long leaveDays = recentAttendances.stream()
                .filter(a -> "LEAVE".equals(a.getStatus()))
                .count();
        
        double attendancePercentage = totalDays > 0 ? (presentDays * 100.0) / totalDays : 0;
        
        return new AttendanceStats(totalDays, presentDays, absentDays, halfDays, leaveDays, 
                                 Math.round(attendancePercentage * 100.0) / 100.0);
    }
    
    private AttendanceResponse convertToResponse(Attendance attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());
        response.setUsername(attendance.getUsername());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setCheckInTime(attendance.getCheckInTime());
        response.setCheckOutTime(attendance.getCheckOutTime());
        response.setStatus(attendance.getStatus());
        response.setWorkingHours(attendance.getWorkingHours());
        response.setNotes(attendance.getNotes());
        return response;
    }
    
    // Inner class for stats
    public static class AttendanceStats {
        private long totalDays;
        private long presentDays;
        private long absentDays;
        private long halfDays;
        private long leaveDays;
        private double attendancePercentage;
        
        public AttendanceStats(long totalDays, long presentDays, long absentDays, 
                              long halfDays, long leaveDays, double attendancePercentage) {
            this.totalDays = totalDays;
            this.presentDays = presentDays;
            this.absentDays = absentDays;
            this.halfDays = halfDays;
            this.leaveDays = leaveDays;
            this.attendancePercentage = attendancePercentage;
        }
        
        // Getters
        public long getTotalDays() { return totalDays; }
        public long getPresentDays() { return presentDays; }
        public long getAbsentDays() { return absentDays; }
        public long getHalfDays() { return halfDays; }
        public long getLeaveDays() { return leaveDays; }
        public double getAttendancePercentage() { return attendancePercentage; }
    }
}