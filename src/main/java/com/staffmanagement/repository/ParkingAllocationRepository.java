package com.staffmanagement.repository;

import com.staffmanagement.model.ParkingAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingAllocationRepository extends JpaRepository<ParkingAllocation, Long> {
    
    Optional<ParkingAllocation> findByUsernameAndStatus(String username, String status);
    
    Optional<ParkingAllocation> findBySlotNumberAndStatus(String slotNumber, String status);
    
    Optional<ParkingAllocation> findByVehicleNumber(String vehicleNumber);
    
    List<ParkingAllocation> findByUsernameOrderByValidFromDesc(String username);
    
    List<ParkingAllocation> findByStatusOrderByValidFromDesc(String status);
    
    List<ParkingAllocation> findBySlotNumberOrderByValidFromDesc(String slotNumber);
    
    @Query("SELECT pa FROM ParkingAllocation pa WHERE pa.status = 'ACTIVE' AND pa.validUntil < :currentDate")
    List<ParkingAllocation> findExpiredAllocations(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT pa FROM ParkingAllocation pa WHERE pa.status = 'ACTIVE' AND (pa.validUntil IS NULL OR pa.validUntil >= :currentDate)")
    List<ParkingAllocation> findActiveAllocations(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT pa FROM ParkingAllocation pa WHERE pa.username = :username AND pa.status = 'ACTIVE' AND (pa.validUntil IS NULL OR pa.validUntil >= :currentDate)")
    Optional<ParkingAllocation> findActiveAllocationByUser(@Param("username") String username, @Param("currentDate") LocalDate currentDate);
    
    long countByStatus(String status);
}