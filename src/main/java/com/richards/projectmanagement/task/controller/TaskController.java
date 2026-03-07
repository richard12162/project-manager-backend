package com.richards.projectmanagement.task.controller;

import com.richards.projectmanagement.task.dto.CreateTaskRequest;
import com.richards.projectmanagement.task.dto.TaskResponse;
import com.richards.projectmanagement.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @RequestBody @Valid CreateTaskRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(201).body(taskService.createTask(projectId, request, authentication));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasksByProject(
            @PathVariable UUID projectId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, authentication));
    }
}