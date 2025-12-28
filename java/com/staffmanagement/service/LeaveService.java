package com.staffmanagement.service;

import com.staffmanagement.model.Leave;
import com.staffmanagement.model.LeavePolicy;
import com.staffmanagement.repository.LeaveRepository;
import com.staffmanagement.repository.LeavePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    public Leave applyForLeave(Leave leaveRequest) {
        // Validate leave request
        validateLeaveRequest(leaveRequest);

        // Calculate number of days
        long days = ChronoUnit.DAYS.between(
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate()
        ) + 1;
        leaveRequest.setNumberOfDays((int) days);

        // Check leave balance (skips OTHER)
        checkLeaveBalance(
                leaveRequest.getUsername(),
                leaveRequest.getLeaveType(),
                leaveRequest.getNumberOfDays()
        );

        // Check for overlapping leaves
        checkOverlappingLeaves(leaveRequest);

        return leaveRepository.save(leaveRequest);
    }

    public Leave approveLeave(Long leaveId, String approvedBy, String comments) {
        Optional<Leave> leaveOpt = leaveRepository.findById(leaveId);
        if (leaveOpt.isPresent()) {
            Leave leave = leaveOpt.get();
            leave.setStatus("APPROVED");
            leave.setApprovedBy(approvedBy);
            leave.setApprovalDate(LocalDate.now());
            if (comments != null) {
                leave.setRejectionReason(comments);
            }
            return leaveRepository.save(leave);
        }
        throw new RuntimeException("Leave request not found");
    }

    public Leave rejectLeave(Long leaveId, String rejectedBy, String reason) {
        Optional<Leave> leaveOpt = leaveRepository.findById(leaveId);
        if (leaveOpt.isPresent()) {
            Leave leave = leaveOpt.get();
            leave.setStatus("REJECTED");
            leave.setApprovedBy(rejectedBy);
            leave.setApprovalDate(LocalDate.now());
            leave.setRejectionReason(reason);
            return leaveRepository.save(leave);
        }
        throw new RuntimeException("Leave request not found");
    }

    public List<Leave> getUserLeaves(String username) {
        return leaveRepository.findByUsernameOrderByAppliedDateDesc(username);
    }

    public List<Leave> getPendingLeaves() {
        return leaveRepository.findByStatusOrderByAppliedDateDesc("PENDING");
    }

    public List<Leave> getLeavesByDateRange(LocalDate startDate, LocalDate endDate) {
        return leaveRepository.findLeavesBetweenDates(startDate, endDate);
    }

    public LeaveBalance getLeaveBalance(String username) {
        LeaveBalance balance = new LeaveBalance();

        // Get all active leave policies
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
                default:
                    // OTHER or any future type not tracked in balance
                    break;
            }
        }

        return balance;
    }

    private void validateLeaveRequest(Leave leave) {
        if (leave.getStartDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Leave start date cannot be in the past");
        }

        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new RuntimeException("Leave end date cannot be before start date");
        }

        // OTHER leave type: no policy limits
        if ("OTHER".equalsIgnoreCase(leave.getLeaveType())) {
            return;
        }

        // Check against leave policy for all other types
        Optional<LeavePolicy> policyOpt = leavePolicyRepository.findByLeaveType(leave.getLeaveType());
        if (policyOpt.isPresent()) {
            LeavePolicy policy = policyOpt.get();

            // Check advance notice
            if (policy.getAdvanceNoticeDays() != null) {
                long noticeDays = ChronoUnit.DAYS.between(LocalDate.now(), leave.getStartDate());
                if (noticeDays < policy.getAdvanceNoticeDays()) {
                    throw new RuntimeException(
                            "This leave requires " + policy.getAdvanceNoticeDays() + " days advance notice"
                    );
                }
            }

            // Check consecutive days
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
        // OTHER leave type has no balance limits
        if ("OTHER".equalsIgnoreCase(leaveType)) {
            return;
        }

        LeaveBalance balance = getLeaveBalance(username);
        int available = 0;

        switch (leaveType) {
            case "SICK":
                available = balance.getSickLeaves();
                break;
            case "CASUAL":
                available = balance.getCasualLeaves();
                break;
            case "EARNED":
                available = balance.getEarnedLeaves();
                break;
            case "MATERNITY":
                available = balance.getMaternityLeaves();
                break;
            case "PATERNITY":
                available = balance.getPaternityLeaves();
                break;
            default:
                // any unknown type: treat as no tracked balance
                available = Integer.MAX_VALUE;
                break;
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

    // Inner class for leave balance
    public static class LeaveBalance {
        private int sickLeaves;
        private int casualLeaves;
        private int earnedLeaves;
        private int maternityLeaves;
        private int paternityLeaves;

        public int getSickLeaves() {
            return sickLeaves;
        }
        public void setSickLeaves(int sickLeaves) {
            this.sickLeaves = sickLeaves;
        }

        public int getCasualLeaves() {
            return casualLeaves;
        }
        public void setCasualLeaves(int casualLeaves) {
            this.casualLeaves = casualLeaves;
        }

        public int getEarnedLeaves() {
            return earnedLeaves;
        }
        public void setEarnedLeaves(int earnedLeaves) {
            this.earnedLeaves = earnedLeaves;
        }

        public int getMaternityLeaves() {
            return maternityLeaves;
        }
        public void setMaternityLeaves(int maternityLeaves) {
            this.maternityLeaves = maternityLeaves;
        }

        public int getPaternityLeaves() {
            return paternityLeaves;
        }
        public void setPaternityLeaves(int paternityLeaves) {
            this.paternityLeaves = paternityLeaves;
        }
    }
}
