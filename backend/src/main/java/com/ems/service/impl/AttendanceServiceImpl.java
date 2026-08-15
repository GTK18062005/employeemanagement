package com.ems.service.impl;

import com.ems.dto.request.CreateAttendanceRequest;
import com.ems.dto.request.UpdateAttendanceStatusRequest;
import com.ems.dto.response.AttendanceResponse;
import com.ems.entity.Attendance;
import com.ems.entity.Employee;
import com.ems.entity.Project;
import com.ems.entity.ProjectAssignment;
import com.ems.entity.ProjectAssignmentStatus;
import com.ems.exception.DuplicateResourceException;
import com.ems.exception.ResourceNotFoundException;
import com.ems.repository.AttendanceRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.ProjectAssignmentRepository;
import com.ems.repository.ProjectRepository;
import com.ems.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final ProjectAssignmentRepository projectAssignmentRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 EmployeeRepository employeeRepository,
                                 ProjectRepository projectRepository,
                                 ProjectAssignmentRepository projectAssignmentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.projectAssignmentRepository = projectAssignmentRepository;
    }

    @Override
    @Transactional
    public AttendanceResponse markAttendance(String username, CreateAttendanceRequest request) {
        if (request.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot mark attendance for a future date");
        }

        Employee employee = getEmployeeByUsername(username);

        if (attendanceRepository.existsByEmployeeIdAndDate(employee.getId(), request.getDate())) {
            throw new DuplicateResourceException("Attendance already exists for this employee on this date");
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(request.getDate());
        attendance.setStatus(request.getStatus());

        return mapToResponse(attendanceRepository.save(attendance));
    }

    @Override
    public List<AttendanceResponse> getMyAttendance(String username) {
        Employee employee = getEmployeeByUsername(username);
        return attendanceRepository.findByEmployeeId(employee.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceResponse getMyAttendanceByDate(String username, LocalDate date) {
        Employee employee = getEmployeeByUsername(username);
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), date)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found for this date"));
        return mapToResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getManagerTeamAttendance(String managerUsername) {
        List<Long> teamEmployeeIds = getTeamEmployeeIds(managerUsername);
        if (teamEmployeeIds.isEmpty()) return List.of();
        
        return attendanceRepository.findByEmployeeIdIn(teamEmployeeIds).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getManagerTeamAttendanceByDate(String managerUsername, LocalDate date) {
        List<Long> teamEmployeeIds = getTeamEmployeeIds(managerUsername);
        if (teamEmployeeIds.isEmpty()) return List.of();
        
        return attendanceRepository.findByEmployeeIdInAndDate(teamEmployeeIds, date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceResponse> getEmployeeAttendance(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found");
        }
        return attendanceRepository.findByEmployeeId(employeeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceResponse getEmployeeAttendanceByDate(Long employeeId, LocalDate date) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found");
        }
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, date)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found for this date"));
        return mapToResponse(attendance);
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendanceStatus(Long attendanceId, UpdateAttendanceStatusRequest request) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));
        attendance.setStatus(request.getStatus());
        return mapToResponse(attendanceRepository.save(attendance));
    }

    private Employee getEmployeeByUsername(String username) {
        return employeeRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Employee profile not found"));
    }

    private List<Long> getTeamEmployeeIds(String managerUsername) {
        Employee manager = getEmployeeByUsername(managerUsername);
        List<Project> projects = projectRepository.findByManagerId(manager.getId());
        
        return projects.stream()
                .flatMap(project -> projectAssignmentRepository.findByProjectId(project.getId()).stream())
                .filter(assignment -> assignment.getStatus() == ProjectAssignmentStatus.ACTIVE)
                .map(assignment -> assignment.getEmployee().getId())
                .distinct()
                .collect(Collectors.toList());
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());
        response.setEmployeeId(attendance.getEmployee().getId());
        response.setEmployeeName(attendance.getEmployee().getFirstName() + " " + attendance.getEmployee().getLastName());
        response.setEmployeeCode(attendance.getEmployee().getEmployeeCode());
        response.setDate(attendance.getDate());
        response.setStatus(attendance.getStatus().name());
        response.setCreatedAt(attendance.getCreatedAt());
        response.setUpdatedAt(attendance.getUpdatedAt());
        return response;
    }
}
