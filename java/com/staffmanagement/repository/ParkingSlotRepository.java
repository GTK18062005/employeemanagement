package com.staffmanagement.repository;

import com.staffmanagement.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    
    Optional<ParkingSlot> findBySlotNumber(String slotNumber);
    
    List<ParkingSlot> findByStatusOrderBySlotNumber(String status);
    
    List<ParkingSlot> findBySlotTypeOrderBySlotNumber(String slotType);
    
    List<ParkingSlot> findByFloorLevelOrderBySlotNumber(String floorLevel);
    
    List<ParkingSlot> findByZoneOrderBySlotNumber(String zone);
    
    List<ParkingSlot> findByVehicleTypeOrderBySlotNumber(String vehicleType);
    
    List<ParkingSlot> findByHasChargingTrue();
    
    List<ParkingSlot> findByIsCoveredTrue();
    
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.status = 'AVAILABLE' AND ps.vehicleType = :vehicleType")
    List<ParkingSlot> findAvailableSlotsByVehicleType(@Param("vehicleType") String vehicleType);
    
    @Query("SELECT ps FROM ParkingSlot ps WHERE ps.status = 'AVAILABLE' AND ps.slotType = :slotType")
    List<ParkingSlot> findAvailableSlotsByType(@Param("slotType") String slotType);
    
    @Query("SELECT COUNT(ps) FROM ParkingSlot ps WHERE ps.status = 'AVAILABLE'")
    long countAvailableSlots();
    
    @Query("SELECT COUNT(ps) FROM ParkingSlot ps WHERE ps.status = 'OCCUPIED'")
    long countOccupiedSlots();
    
    @Query("SELECT ps.zone, COUNT(ps), SUM(CASE WHEN ps.status = 'AVAILABLE' THEN 1 ELSE 0 END) FROM ParkingSlot ps GROUP BY ps.zone")
    List<Object[]> countSlotsByZone();
    
    @Query("SELECT ps.floorLevel, COUNT(ps), SUM(CASE WHEN ps.status = 'AVAILABLE' THEN 1 ELSE 0 END) FROM ParkingSlot ps GROUP BY ps.floorLevel")
    List<Object[]> countSlotsByFloor();
}