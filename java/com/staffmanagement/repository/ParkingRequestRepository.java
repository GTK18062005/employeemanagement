package com.staffmanagement.repository;

import com.staffmanagement.model.ParkingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingRequestRepository extends JpaRepository<ParkingRequest, Long> {
    
    List<ParkingRequest> findByUsernameOrderByCreatedDateDesc(String username);
    
    List<ParkingRequest> findByStatusOrderByCreatedDateDesc(String status);
    
    List<ParkingRequest> findByRequestTypeOrderByCreatedDateDesc(String requestType);
    
    List<ParkingRequest> findByVehicleNumberOrderByCreatedDateDesc(String vehicleNumber);
    
    @Query("SELECT pr FROM ParkingRequest pr WHERE pr.status = 'PENDING' AND pr.preferredStartDate <= :startDate")
    List<ParkingRequest> findPendingRequestsBeforeDate(@Param("startDate") java.time.LocalDate startDate);
    
    @Query("SELECT COUNT(pr) FROM ParkingRequest pr WHERE pr.status = 'PENDING'")
    long countPendingRequests();
    
    @Query("SELECT pr.requestType, COUNT(pr) FROM ParkingRequest pr GROUP BY pr.requestType")
    List<Object[]> countRequestsByType();
}