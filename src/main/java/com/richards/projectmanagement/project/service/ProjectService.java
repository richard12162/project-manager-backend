package com.richards.projectmanagement.project.service;

import com.richards.projectmanagement.common.exception.ProjectAccessDeniedException;
import com.richards.projectmanagement.project.domain.Project;
import com.richards.projectmanagement.project.domain.ProjectMember;
import com.richards.projectmanagement.project.domain.ProjectRole;
import com.richards.projectmanagement.project.dto.CreateProjectRequest;
import com.richards.projectmanagement.project.dto.ProjectResponse;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        OffsetDateTime now = OffsetDateTime.now();

        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setName(request.name().trim());
        project.setDescription(request.description() != null ? request.description().trim() : null);
        project.setOwner(currentUser);
        project.setCreatedAt(now);
        project.setUpdatedAt(now);

        Project savedProject = projectRepository.save(project);

        ProjectMember ownerMembership = new ProjectMember();
        ownerMembership.setId(UUID.randomUUID());
        ownerMembership.setProject(savedProject);
        ownerMembership.setUser(currentUser);
        ownerMembership.setRole(ProjectRole.OWNER);
        ownerMembership.setCreatedAt(now);

        projectMemberRepository.save(ownerMembership);

        return toResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID projectId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, currentUser.getId());
        if (!isMember) {
            throw new ProjectAccessDeniedException(projectId);
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectAccessDeniedException(projectId));

        return toResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        return projectRepository.findAllByMemberUserId(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getOwner().getEmail(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}