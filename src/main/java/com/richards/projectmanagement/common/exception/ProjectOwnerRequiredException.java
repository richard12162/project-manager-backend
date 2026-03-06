package com.richards.projectmanagement.common.exception;

import java.util.UUID;

public class ProjectOwnerRequiredException extends RuntimeException {

    public ProjectOwnerRequiredException(UUID projectId) {
        super("Owner role required for project: " + projectId);
    }
}