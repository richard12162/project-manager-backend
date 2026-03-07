package com.richards.projectmanagement.task.repository;

import com.richards.projectmanagement.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    boolean existsByIdAndProjectId(UUID taskId, UUID projectId);
}