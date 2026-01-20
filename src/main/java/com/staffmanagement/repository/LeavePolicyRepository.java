package com.staffmanagement.repository;

import com.staffmanagement.model.LeavePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.*;
import java.util.Optional;

@Repository
public interface LeavePolicyRepository extends JpaRepository<LeavePolicy, Long> {
    
    Optional<LeavePolicy> findByLeaveType(String leaveType);
    
    List<LeavePolicy> findByIsActiveTrue();
}