package com.richards.projectmanagement.task.repository;

import com.richards.projectmanagement.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);

    boolean existsByIdAndProjectId(UUID taskId, UUID projectId);
}