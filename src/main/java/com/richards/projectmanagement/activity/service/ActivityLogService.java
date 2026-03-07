package com.richards.projectmanagement.activity.service;

import com.richards.projectmanagement.activity.domain.ActivityLog;
import com.richards.projectmanagement.activity.domain.ActivityType;
import com.richards.projectmanagement.activity.dto.ActivityLogResponse;
import com.richards.projectmanagement.activity.repository.ActivityLogRepository;
import com.richards.projectmanagement.common.exception.ProjectAccessDeniedException;
import com.richards.projectmanagement.common.exception.ProjectNotFoundException;
import com.richards.projectmanagement.project.domain.Project;
import com.richards.projectmanagement.project.repository.ProjectMemberRepository;
import com.richards.projectmanagement.project.repository.ProjectRepository;
import com.richards.projectmanagement.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ActivityLogService(
            ActivityLogRepository activityLogRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.activityLogRepository = activityLogRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional
    public void log(
            Project project,
            User actor,
            ActivityType type,
            UUID entityId,
            String entityType,
            String message
    ) {
        ActivityLog log = new ActivityLog();
        log.setId(UUID.randomUUID());
        log.setProject(project);
        log.setActor(actor);
        log.setType(type);
        log.setEntityId(entityId);
        log.setEntityType(entityType);
        log.setMessage(message);
        log.setCreatedAt(OffsetDateTime.now());

        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getProjectActivity(UUID projectId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new ProjectAccessDeniedException(projectId);
        }

        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }

        return activityLogRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ActivityLogResponse toResponse(ActivityLog log) {
        return new ActivityLogResponse(
                log.getId(),
                log.getProject().getId(),
                log.getActor().getId(),
                log.getActor().getEmail(),
                log.getType().name(),
                log.getEntityId(),
                log.getEntityType(),
                log.getMessage(),
                log.getCreatedAt()
        );
    }
}