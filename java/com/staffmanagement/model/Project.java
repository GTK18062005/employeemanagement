package com.staffmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_code", unique = true, nullable = false)
    private String projectCode;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "project_manager", nullable = false)
    private String projectManager;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status")
    private String status; // PLANNING, IN_PROGRESS, COMPLETED, ON_HOLD, CANCELLED

    @Column(name = "priority")
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    @Column(name = "budget")
    private Double budget;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "technology_stack")
    private String technologyStack;

    @ElementCollection
    @CollectionTable(name = "project_team_members", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "team_member")
    private List<String> teamMembers = new ArrayList<>();

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "last_updated")
    private LocalDate lastUpdated;

    // Constructors
    public Project() {
        this.createdDate = LocalDate.now();
        this.lastUpdated = LocalDate.now();
        this.status = "PLANNING";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectCode() { return projectCode; }
    public void setProjectCode(String projectCode) { this.projectCode = projectCode; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProjectManager() { return projectManager; }
    public void setProjectManager(String projectManager) { this.projectManager = projectManager; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getTechnologyStack() { return technologyStack; }
    public void setTechnologyStack(String technologyStack) { this.technologyStack = technologyStack; }

    public List<String> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<String> teamMembers) { this.teamMembers = teamMembers; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }
}