package com.richards.projectmanagement.common.exception;

import java.util.UUID;

public class TaskAccessDeniedException extends RuntimeException {

    public TaskAccessDeniedException(UUID projectId) {
        super("Access denied to tasks of project: " + projectId);
    }
}