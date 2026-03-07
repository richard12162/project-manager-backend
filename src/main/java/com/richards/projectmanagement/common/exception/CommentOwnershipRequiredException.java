package com.richards.projectmanagement.common.exception;

import java.util.UUID;

public class CommentOwnershipRequiredException extends RuntimeException {

    public CommentOwnershipRequiredException(UUID commentId) {
        super("Only the comment author may modify comment: " + commentId);
    }
}