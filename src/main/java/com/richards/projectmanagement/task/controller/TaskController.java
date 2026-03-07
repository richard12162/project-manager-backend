package com.richards.projectmanagement.task.controller;

import com.richards.projectmanagement.task.dto.CreateTaskRequest;
import com.richards.projectmanagement.task.dto.TaskResponse;
import com.richards.projectmanagement.task.dto.UpdateTaskAssignmentRequest;
import com.richards.projectmanagement.task.dto.UpdateTaskRequest;
import com.richards.projectmanagement.task.dto.UpdateTaskStatusRequest;
import com.richards.projectmanagement.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @RequestBody @Valid CreateTaskRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(201).body(taskService.createTask(projectId, request, authentication));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasksByProject(
            @PathVariable UUID projectId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, authentication));
    }

    @PatchMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID taskId,
            @RequestBody @Valid UpdateTaskRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request, authentication));
    }

    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable UUID taskId,
            @RequestBody @Valid UpdateTaskStatusRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, request, authentication));
    }

    @PatchMapping("/tasks/{taskId}/assignment")
    public ResponseEntity<TaskResponse> updateTaskAssignment(
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskAssignmentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(taskService.updateTaskAssignment(taskId, request, authentication));
    }
}