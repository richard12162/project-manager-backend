package com.richards.projectmanagement.project.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        UUID ownerId,
        String ownerEmail,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}