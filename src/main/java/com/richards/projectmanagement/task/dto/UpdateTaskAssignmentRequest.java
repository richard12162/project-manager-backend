package com.richards.projectmanagement.task.dto;

import java.util.UUID;

public record UpdateTaskAssignmentRequest(
        UUID assigneeId
) {
}