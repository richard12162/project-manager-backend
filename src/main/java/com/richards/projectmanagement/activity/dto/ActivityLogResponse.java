package com.richards.projectmanagement.activity.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ActivityLogResponse(
        UUID id,
        UUID projectId,
        UUID actorId,
        String actorEmail,
        String type,
        UUID entityId,
        String entityType,
        String message,
        OffsetDateTime createdAt
) {
}