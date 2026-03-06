package com.richards.projectmanagement.auth.dto;

import com.richards.projectmanagement.user.domain.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegisterResponse(UUID id, String email, UserRole role, OffsetDateTime createdAt) {
}
