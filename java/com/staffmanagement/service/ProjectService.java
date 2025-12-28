package com.staffmanagement.service;

import com.staffmanagement.model.Project;
import com.staffmanagement.model.ProjectTask;
import com.staffmanagement.repository.ProjectRepository;
import com.staffmanagement.repository.ProjectTaskRepository;
import com.staffmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {
    
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ProjectTaskRepository projectTaskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Project createProject(Project project) {
        // Validate project manager
        if (!isValidProjectManager(project.getProjectManager())) {
            throw new RuntimeException("Project manager must have PROJECT_MANAGER role");
        }
        
        // Validate team members
        validateTeamMembers(project.getTeamMembers());
        
        // Generate project code if not provided
        if (project.getProjectCode() == null) {
            project.setProjectCode(generateProjectCode(project.getProjectName()));
        }
        
        return projectRepository.save(project);
    }
    
    public Project updateProject(Long projectId, Project projectDetails) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            
            // Update fields
            if (projectDetails.getProjectName() != null) {
                project.setProjectName(projectDetails.getProjectName());
            }
            if (projectDetails.getDescription() != null) {
                project.setDescription(projectDetails.getDescription());
            }
            if (projectDetails.getStartDate() != null) {
                project.setStartDate(projectDetails.getStartDate());
            }
            if (projectDetails.getEndDate() != null) {
                project.setEndDate(projectDetails.getEndDate());
            }
            if (projectDetails.getStatus() != null) {
                project.setStatus(projectDetails.getStatus());
            }
            if (projectDetails.getPriority() != null) {
                project.setPriority(projectDetails.getPriority());
            }
            if (projectDetails.getBudget() != null) {
                project.setBudget(projectDetails.getBudget());
            }
            if (projectDetails.getClientName() != null) {
                project.setClientName(projectDetails.getClientName());
            }
            if (projectDetails.getTechnologyStack() != null) {
                project.setTechnologyStack(projectDetails.getTechnologyStack());
            }
            if (projectDetails.getTeamMembers() != null) {
                validateTeamMembers(projectDetails.getTeamMembers());
                project.setTeamMembers(projectDetails.getTeamMembers());
            }
            
            project.setLastUpdated(LocalDate.now());
            return projectRepository.save(project);
        }
        throw new RuntimeException("Project not found");
    }
    
    public ProjectTask createTask(ProjectTask task) {
        // Validate assignee exists
        if (!userRepository.existsByUsername(task.getAssignedTo())) {
            throw new RuntimeException("Assignee not found");
        }
        
        // Validate project exists
        if (!projectRepository.existsById(task.getProjectId())) {
            throw new RuntimeException("Project not found");
        }
        
        return projectTaskRepository.save(task);
    }
    
    public ProjectTask updateTaskStatus(Long taskId, String status, Integer completionPercentage) {
        Optional<ProjectTask> taskOpt = projectTaskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            ProjectTask task = taskOpt.get();
            task.setStatus(status);
            if (completionPercentage != null) {
                task.setCompletionPercentage(completionPercentage);
            }
            if ("COMPLETED".equals(status)) {
                task.setActualEndDate(LocalDate.now());
                task.setCompletionPercentage(100);
            }
            return projectTaskRepository.save(task);
        }
        throw new RuntimeException("Task not found");
    }
    
    public List<Project> getProjectsByManager(String projectManager) {
        return projectRepository.findByProjectManagerOrderByCreatedDateDesc(projectManager);
    }
    
    public List<Project> getUserProjects(String username) {
        return projectRepository.findUserProjects(username, username);
    }
    
    public List<ProjectTask> getProjectTasks(Long projectId) {
        return projectTaskRepository.findByProjectIdOrderByDueDateAsc(projectId);
    }
    
    public List<ProjectTask> getUserTasks(String username) {
        return projectTaskRepository.findByAssignedToOrderByDueDateAsc(username);
    }
    
    public ProjectStats getProjectStats(Long projectId) {
        List<Object[]> statusCounts = projectTaskRepository.countTasksByStatusForProject(projectId);
        
        ProjectStats stats = new ProjectStats();
        stats.setTotalTasks(0);
        
        for (Object[] statusCount : statusCounts) {
            String status = (String) statusCount[0];
            Long count = (Long) statusCount[1];
            stats.setTotalTasks(stats.getTotalTasks() + count.intValue());
            
            switch (status) {
                case "COMPLETED":
                    stats.setCompletedTasks(count.intValue());
                    break;
                case "IN_PROGRESS":
                    stats.setInProgressTasks(count.intValue());
                    break;
                case "REVIEW":
                    stats.setReviewTasks(count.intValue());
                    break;
                case "TODO":
                    stats.setTodoTasks(count.intValue());
                    break;
            }
        }
        
        if (stats.getTotalTasks() > 0) {
            stats.setCompletionPercentage((stats.getCompletedTasks() * 100) / stats.getTotalTasks());
        }
        
        return stats;
    }
    
    private boolean isValidProjectManager(String username) {
        return userRepository.findByUsername(username)
                .map(user -> "PROJECT_MANAGER".equals(user.getRole()) || "ADMIN".equals(user.getRole()))
                .orElse(false);
    }
    
    private void validateTeamMembers(List<String> teamMembers) {
        for (String member : teamMembers) {
            if (!userRepository.existsByUsername(member)) {
                throw new RuntimeException("Team member not found: " + member);
            }
        }
    }
    
    private String generateProjectCode(String projectName) {
        String code = projectName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (code.length() > 6) {
            code = code.substring(0, 6);
        }
        return code + "-" + System.currentTimeMillis() % 10000;
    }
    
    // Inner class for project statistics
    public static class ProjectStats {
        private int totalTasks;
        private int completedTasks;
        private int inProgressTasks;
        private int reviewTasks;
        private int todoTasks;
        private int completionPercentage;
        
        // Getters and Setters
        public int getTotalTasks() { return totalTasks; }
        public void setTotalTasks(int totalTasks) { this.totalTasks = totalTasks; }
        
        public int getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(int completedTasks) { this.completedTasks = completedTasks; }
        
        public int getInProgressTasks() { return inProgressTasks; }
        public void setInProgressTasks(int inProgressTasks) { this.inProgressTasks = inProgressTasks; }
        
        public int getReviewTasks() { return reviewTasks; }
        public void setReviewTasks(int reviewTasks) { this.reviewTasks = reviewTasks; }
        
        public int getTodoTasks() { return todoTasks; }
        public void setTodoTasks(int todoTasks) { this.todoTasks = todoTasks; }
        
        public int getCompletionPercentage() { return completionPercentage; }
        public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }
    }
}