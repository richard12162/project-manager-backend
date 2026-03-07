package com.richards.projectmanagement.task.dto;

import com.richards.projectmanagement.task.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTaskStatusRequest(

        @NotNull(message = "status is required")
        TaskStatus status

) {
}