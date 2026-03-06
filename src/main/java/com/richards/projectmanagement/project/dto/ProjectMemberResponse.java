package com.richards.projectmanagement.project.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectMemberResponse(
        UUID id,
        UUID userId,
        String email,
        String role,
        OffsetDateTime joinedAt
) {}