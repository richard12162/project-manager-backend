package com.richards.projectmanagement.common.exception;

import java.util.UUID;

public class CommentAccessDeniedException extends RuntimeException {

    public CommentAccessDeniedException(UUID taskId) {
        super("Access denied to comments of task: " + taskId);
    }
}