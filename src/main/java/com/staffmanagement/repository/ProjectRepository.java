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

    // ✅ 1. Manager of the project
    List<Project> findByProjectManager(String username);

    // ✅ 2. User is in teamMembers (JOIN query)
    @Query("SELECT DISTINCT p FROM Project p JOIN p.teamMembers tm WHERE tm = :username")
    List<Project> findByTeamMemberUsername(@Param("username") String username);

    // ✅ 3. ALL PROJECTS FOR USER (manager OR team member) - FIXED
    @Query("SELECT DISTINCT p FROM Project p WHERE p.projectManager = :username " +
           "OR EXISTS (SELECT 1 FROM p.teamMembers tm WHERE tm = :username)")
    List<Project> findByAssignedUser(@Param("username") String username);

    // ✅ 4. Find by unique project code
    Optional<Project> findByProjectCode(String projectCode);

    // ✅ 5. Find by status
    List<Project> findByStatusOrderByCreatedDateDesc(String status);
}
