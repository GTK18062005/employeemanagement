package com.staffmanagement.repository;

import com.staffmanagement.model.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    
    List<ProjectTask> findByProjectIdOrderByDueDateAsc(Long projectId);
    
    List<ProjectTask> findByAssignedToOrderByDueDateAsc(String assignedTo);
    
    List<ProjectTask> findByProjectIdAndStatusOrderByDueDateAsc(Long projectId, String status);
    
    List<ProjectTask> findByAssignedToAndStatusOrderByDueDateAsc(String assignedTo, String status);
    
    @Query("SELECT pt FROM ProjectTask pt WHERE pt.projectId = :projectId AND pt.assignedTo = :assignedTo")
    List<ProjectTask> findByProjectAndAssignee(@Param("projectId") Long projectId, @Param("assignedTo") String assignedTo);
    
    long countByProjectIdAndStatus(Long projectId, String status);
    
    @Query("SELECT COUNT(pt), pt.status FROM ProjectTask pt WHERE pt.projectId = :projectId GROUP BY pt.status")
    List<Object[]> countTasksByStatusForProject(@Param("projectId") Long projectId);
}