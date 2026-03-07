package com.richards.projectmanagement.common.exception;

import java.util.UUID;

public class InvalidTaskAssigneeException extends RuntimeException {

    public InvalidTaskAssigneeException(UUID assigneeId, UUID projectId) {
        super("User " + assigneeId + " is not a member of project: " + projectId);
    }
}