package com.richards.projectmanagement.task.controller;

import com.richards.projectmanagement.common.dto.PagedResponse;
import com.richards.projectmanagement.task.domain.TaskPriority;
import com.richards.projectmanagement.task.domain.TaskStatus;
import com.richards.projectmanagement.task.dto.*;
import com.richards.projectmanagement.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PagedResponse<TaskResponse>> getTasksByProject(
            @PathVariable UUID projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAtDesc") String sort,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                taskService.getTasksByProject(
                        projectId,
                        status,
                        priority,
                        assigneeId,
                        page,
                        size,
                        sort,
                        authentication
                )
        );
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