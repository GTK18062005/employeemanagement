package com.staffmanagement.repository;

import com.staffmanagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    Optional<Project> findByProjectCode(String projectCode);
    
    List<Project> findByProjectManagerOrderByCreatedDateDesc(String projectManager);
    
    List<Project> findByStatusOrderByCreatedDateDesc(String status);
    
    List<Project> findByPriorityOrderByCreatedDateDesc(String priority);
    
    @Query("SELECT p FROM Project p WHERE :teamMember MEMBER OF p.teamMembers")
    List<Project> findByTeamMember(@Param("teamMember") String teamMember);
    
    @Query("SELECT p FROM Project p WHERE p.projectManager = :projectManager OR :username MEMBER OF p.teamMembers")
    List<Project> findUserProjects(@Param("projectManager") String projectManager, @Param("username") String username);
    
    long countByStatus(String status);
}