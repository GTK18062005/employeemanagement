package com.staffmanagement.controller;

import com.staffmanagement.model.Project;
import com.staffmanagement.model.ProjectTask;
import com.staffmanagement.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:3000")
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        try {
            Project savedProject = projectService.createProject(project);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Project created successfully");
            response.put("project", savedProject);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable Long projectId, @RequestBody Project projectDetails) {
        try {
            Project updatedProject = projectService.updateProject(projectId, projectDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Project updated successfully");
            response.put("project", updatedProject);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestBody ProjectTask task) {
        try {
            ProjectTask savedTask = projectService.createTask(task);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task created successfully");
            response.put("task", savedTask);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long taskId, @RequestBody Map<String, Object> request) {
        try {
            String status = (String) request.get("status");
            Integer completionPercentage = (Integer) request.get("completionPercentage");
            
            ProjectTask updatedTask = projectService.updateTaskStatus(taskId, status, completionPercentage);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Task status updated successfully");
            response.put("task", updatedTask);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/manager/{projectManager}")
    public ResponseEntity<?> getProjectsByManager(@PathVariable String projectManager) {
        try {
            List<Project> projects = projectService.getProjectsByManager(projectManager);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("projects", projects);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserProjects(@PathVariable String username) {
        try {
            List<Project> projects = projectService.getUserProjects(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("projects", projects);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{projectId}/tasks")
    public ResponseEntity<?> getProjectTasks(@PathVariable Long projectId) {
        try {
            List<ProjectTask> tasks = projectService.getProjectTasks(projectId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tasks", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{username}/tasks")
    public ResponseEntity<?> getUserTasks(@PathVariable String username) {
        try {
            List<ProjectTask> tasks = projectService.getUserTasks(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tasks", tasks);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/{projectId}/stats")
    public ResponseEntity<?> getProjectStats(@PathVariable Long projectId) {
        try {
            ProjectService.ProjectStats stats = projectService.getProjectStats(projectId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}