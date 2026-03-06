package com.richards.projectmanagement.common.exception;

import java.util.UUID;

public class ProjectMemberAlreadyExistsException extends RuntimeException {

    public ProjectMemberAlreadyExistsException(UUID projectId, String email) {
        super("User with email " + email + " is already a member of project: " + projectId);
    }
}