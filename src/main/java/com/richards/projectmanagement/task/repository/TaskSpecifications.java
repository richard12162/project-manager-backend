package com.richards.projectmanagement.task.repository;

import com.richards.projectmanagement.task.domain.Task;
import com.richards.projectmanagement.task.domain.TaskPriority;
import com.richards.projectmanagement.task.domain.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class TaskSpecifications {

    private TaskSpecifications() {
    }

    public static Specification<Task> hasProjectId(UUID projectId) {
        return (root, query, cb) -> cb.equal(root.get("project").get("id"), projectId);
    }

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> hasPriority(TaskPriority priority) {
        return (root, query, cb) ->
                priority == null ? null : cb.equal(root.get("priority"), priority);
    }

    public static Specification<Task> hasAssigneeId(UUID assigneeId) {
        return (root, query, cb) ->
                assigneeId == null ? null : cb.equal(root.get("assignee").get("id"), assigneeId);
    }
}