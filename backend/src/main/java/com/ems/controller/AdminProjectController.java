package com.ems.controller;

import com.ems.dto.request.ProjectCreateRequest;
import com.ems.dto.request.ProjectManagerChangeRequest;
import com.ems.dto.request.ProjectStatusRequest;
import com.ems.dto.request.ProjectUpdateRequest;
import com.ems.dto.response.ProjectResponse;
import com.ems.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/projects")
public class AdminProjectController {

    private final ProjectService projectService;

    public AdminProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectUpdateRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @PatchMapping("/{id}/manager")
    public ResponseEntity<ProjectResponse> changeProjectManager(@PathVariable Long id, @Valid @RequestBody ProjectManagerChangeRequest request) {
        return ResponseEntity.ok(projectService.changeProjectManager(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponse> changeProjectStatus(@PathVariable Long id, @Valid @RequestBody ProjectStatusRequest request) {
        return ResponseEntity.ok(projectService.changeProjectStatus(id, request));
    }
}
