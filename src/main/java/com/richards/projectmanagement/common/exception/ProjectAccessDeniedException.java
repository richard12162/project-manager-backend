package com.richards.projectmanagement.common.exception;

import java.util.UUID;

public class ProjectAccessDeniedException extends RuntimeException {

    public ProjectAccessDeniedException(UUID projectId) {
        super("Access denied to project: " + projectId);
    }
}