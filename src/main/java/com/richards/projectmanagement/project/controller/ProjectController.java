package com.richards.projectmanagement.project.controller;

import com.richards.projectmanagement.project.dto.CreateProjectRequest;
import com.richards.projectmanagement.project.dto.ProjectResponse;
import com.richards.projectmanagement.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @RequestBody @Valid CreateProjectRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(201).body(projectService.createProject(request, authentication));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable UUID projectId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(projectService.getProjectById(projectId, authentication));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getMyProjects(Authentication authentication) {
        return ResponseEntity.ok(projectService.getMyProjects(authentication));
    }
}