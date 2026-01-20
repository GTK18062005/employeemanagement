package com.staffmanagement.service;

import com.staffmanagement.model.Leave;
import com.staffmanagement.model.LeavePolicy;
import com.staffmanagement.repository.LeaveRepository;
import com.staffmanagement.repository.LeavePolicyRepository;
import com.staffmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ EMPLOYEE: Apply for leave
    public Leave applyForLeave(Leave leaveRequest) {
        validateLeaveRequest(leaveRequest);

        long days = ChronoUnit.DAYS.between(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        ) + 1;
        leaveRequest.setNumberOfDays((int) days);

        checkLeaveBalance(
                leaveRequest.getUsername(),
                leaveRequest.getLeaveType(),
                leaveRequest.getNumberOfDays()
        );

        checkOverlappingLeaves(leaveRequest);

        // Set applied date if not set
        if (leaveRequest.getAppliedDate() == null) {
            leaveRequest.setAppliedDate(LocalDate.now());
        }
        
        // Set default status if not set
        if (leaveRequest.getStatus() == null) {
            leaveRequest.setStatus("PENDING");
        }

        return leaveRepository.save(leaveRequest);
    }

    // ✅ EMPLOYEE: View own leaves
    public List<Leave> getUserLeaves(String username) {
        return leaveRepository.findByUsernameOrderByAppliedDateDesc(username);
    }

    // ✅ MANAGER: View pending leaves for team (FIXED)
    public List<Leave> getPendingLeavesForManager(String managerUsername) {
        return leaveRepository.findPendingLeavesForManagerTeam(managerUsername);
    }

    // ✅ MANAGER: Approve leave
    public Leave approveLeave(Long leaveId, String approvedBy, String comments) {
        Optional<Leave> leaveOpt = leaveRepository.findById(leaveId);
        if (leaveOpt.isPresent()) {
            Leave leave = leaveOpt.get();
            
            if (!"PENDING".equals(leave.getStatus())) {
                throw new RuntimeException("Only PENDING leaves can be approved");
            }

            leave.setStatus("APPROVED");
            leave.setApprovedBy(approvedBy);
            leave.setApprovalDate(LocalDateTime.now());
            if (comments != null) {
                leave.setRejectionReason(comments);
            }
            return leaveRepository.save(leave);
        }
        throw new RuntimeException("Leave request not found");
    }

    // ✅ MANAGER: Reject leave
    public Leave rejectLeave(Long leaveId, String rejectedBy, String reason) {
        Optional<Leave> leaveOpt = leaveRepository.findById(leaveId);
        if (leaveOpt.isPresent()) {
            Leave leave = leaveOpt.get();
            
            if (!"PENDING".equals(leave.getStatus())) {
                throw new RuntimeException("Only PENDING leaves can be rejected");
            }

            leave.setStatus("REJECTED");
            leave.setApprovedBy(rejectedBy);
            leave.setApprovalDate(LocalDateTime.now());
            leave.setRejectionReason(reason);
            return leaveRepository.save(leave);
        }
        throw new RuntimeException("Leave request not found");
    }

    // ✅ Existing methods
    public List<Leave> getPendingLeaves() {
        return leaveRepository.findByStatusOrderByAppliedDateDesc("PENDING");
    }

    public List<Leave> getLeavesByDateRange(LocalDate startDate, LocalDate endDate) {
        return leaveRepository.findLeavesBetweenDates(startDate, endDate);
    }

    public LeaveBalance getLeaveBalance(String username) {
        LeaveBalance balance = new LeaveBalance();

        List<LeavePolicy> policies = leavePolicyRepository.findByIsActiveTrue();

        for (LeavePolicy policy : policies) {
            long usedLeaves = leaveRepository
                    .countByUsernameAndLeaveTypeAndStatus(
                            username,
                            policy.getLeaveType(),
                            "APPROVED"
                    );
            int available = policy.getAnnualEntitlement() - (int) usedLeaves;

            switch (policy.getLeaveType()) {
                case "SICK":
                    balance.setSickLeaves(available);
                    break;
                case "CASUAL":
                    balance.setCasualLeaves(available);
                    break;
                case "EARNED":
                    balance.setEarnedLeaves(available);
                    break;
                case "MATERNITY":
                    balance.setMaternityLeaves(available);
                    break;
                case "PATERNITY":
                    balance.setPaternityLeaves(available);
                    break;
            }
        }

        return balance;
    }

    // ✅ Validation methods
    private void validateLeaveRequest(Leave leave) {
        if (leave.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Leave start date cannot be in the past");
        }

        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new RuntimeException("Leave end date cannot be before start date");
        }

        if ("OTHER".equalsIgnoreCase(leave.getLeaveType())) {
            return;
        }

        Optional<LeavePolicy> policyOpt = leavePolicyRepository.findByLeaveType(leave.getLeaveType());
        if (policyOpt.isPresent()) {
            LeavePolicy policy = policyOpt.get();

            if (policy.getAdvanceNoticeDays() != null) {
                long noticeDays = ChronoUnit.DAYS.between(LocalDate.now(), leave.getStartDate());
                if (noticeDays < policy.getAdvanceNoticeDays()) {
                    throw new RuntimeException(
                            "This leave requires " + policy.getAdvanceNoticeDays() + " days advance notice"
                    );
                }
            }

            long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
            if (policy.getMaxConsecutiveDays() != null && days > policy.getMaxConsecutiveDays()) {
                throw new RuntimeException(
                        "Maximum consecutive days for " + leave.getLeaveType() +
                        " leave is " + policy.getMaxConsecutiveDays()
                );
            }
        }
    }

    private void checkLeaveBalance(String username, String leaveType, int requestedDays) {
        if ("OTHER".equalsIgnoreCase(leaveType)) {
            return;
        }

        LeaveBalance balance = getLeaveBalance(username);
        int available = 0;

        switch (leaveType) {
            case "SICK": available = balance.getSickLeaves(); break;
            case "CASUAL": available = balance.getCasualLeaves(); break;
            case "EARNED": available = balance.getEarnedLeaves(); break;
            case "MATERNITY": available = balance.getMaternityLeaves(); break;
            case "PATERNITY": available = balance.getPaternityLeaves(); break;
            default: available = Integer.MAX_VALUE; break;
        }

        if (requestedDays > available) {
            throw new RuntimeException(
                    "Insufficient " + leaveType +
                    " leave balance. Available: " + available +
                    ", Requested: " + requestedDays
            );
        }
    }

    private void checkOverlappingLeaves(Leave newLeave) {
        List<Leave> existingLeaves = leaveRepository
                .findApprovedLeavesByUserAndDateRange(
                        newLeave.getUsername(),
                        newLeave.getStartDate(),
                        newLeave.getEndDate()
                );

        if (!existingLeaves.isEmpty()) {
            throw new RuntimeException("You already have approved leaves during this period");
        }
    }

    // ✅ LeaveBalance class
    public static class LeaveBalance {
        private int sickLeaves;
        private int casualLeaves;
        private int earnedLeaves;
        private int maternityLeaves;
        private int paternityLeaves;

        public int getSickLeaves() { return sickLeaves; }
        public void setSickLeaves(int sickLeaves) { this.sickLeaves = sickLeaves; }
        public int getCasualLeaves() { return casualLeaves; }
        public void setCasualLeaves(int casualLeaves) { this.casualLeaves = casualLeaves; }
        public int getEarnedLeaves() { return earnedLeaves; }
        public void setEarnedLeaves(int earnedLeaves) { this.earnedLeaves = earnedLeaves; }
        public int getMaternityLeaves() { return maternityLeaves; }
        public void setMaternityLeaves(int maternityLeaves) { this.maternityLeaves = maternityLeaves; }
        public int getPaternityLeaves() { return paternityLeaves; }
        public void setPaternityLeaves(int paternityLeaves) { this.paternityLeaves = paternityLeaves; }
    }
}