package com.richards.projectmanagement.activity.repository;

import com.richards.projectmanagement.activity.domain.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    List<ActivityLog> findAllByProjectIdOrderByCreatedAtDesc(UUID projectId);
}