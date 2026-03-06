package com.richards.projectmanagement.auth.dto;

import java.util.UUID;

public record LoginResponse(UUID id, String email, String role, String message) {
}