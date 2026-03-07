package com.richards.projectmanagement.task.dto;

import com.richards.projectmanagement.task.domain.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record UpdateTaskRequest(

        @NotBlank(message = "title is required")
        @Size(max = 255, message = "title must be at most 255 characters")
        String title,

        @Size(max = 5000, message = "description must be at most 5000 characters")
        String description,

        TaskPriority priority,

        OffsetDateTime dueDate

) {}