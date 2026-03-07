package com.richards.projectmanagement.comment.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CommentResponse(
        UUID id,
        UUID taskId,
        UUID authorId,
        String authorEmail,
        String content,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}