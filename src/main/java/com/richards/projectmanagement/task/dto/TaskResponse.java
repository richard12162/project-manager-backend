package com.richards.projectmanagement.task.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String projectName,
        String title,
        String description,
        String status,
        String priority,
        UUID assigneeId,
        String assigneeEmail,
        OffsetDateTime dueDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
