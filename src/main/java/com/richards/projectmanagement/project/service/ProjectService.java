package com.richards.projectmanagement.project.service;

import com.richards.projectmanagement.common.exception.*;
import com.richards.projectmanagement.project.domain.Project;
import com.richards.projectmanagement.project.domain.ProjectMember;
import com.richards.projectmanagement.project.domain.ProjectRole;
import com.richards.projectmanagement.project.dto.AddProjectMemberRequest;
import com.richards.projectmanagement.project.dto.CreateProjectRequest;
import com.richards.projectmanagement.project.dto.ProjectMemberResponse;
import com.richards.projectmanagement.project.dto.ProjectResponse;
import com.richards.projectmanagement.project.repository.ProjectMemberRepository;
import com.richards.projectmanagement.project.repository.ProjectRepository;
import com.richards.projectmanagement.user.domain.User;
import com.richards.projectmanagement.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
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

        return toProjectResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID projectId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        ensureProjectMember(projectId, currentUser.getId());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        return toProjectResponse(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        return projectRepository.findAllByMemberUserId(currentUser.getId())
                .stream()
                .map(this::toProjectResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> getProjectMembers(UUID projectId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        ensureProjectMember(projectId, currentUser.getId());

        ensureProjectExists(projectId);

        return projectMemberRepository.findAllByProjectIdOrderByCreatedAtAsc(projectId)
                .stream()
                .map(this::toProjectMemberResponse)
                .toList();
    }

    @Transactional
    public ProjectMemberResponse addProjectMember(
            UUID projectId,
            AddProjectMemberRequest request,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        ProjectMember currentMembership = projectMemberRepository.findByProjectIdAndUserId(projectId, currentUser.getId())
                .orElseThrow(() -> new ProjectAccessDeniedException(projectId));

        if (currentMembership.getRole() != ProjectRole.OWNER) {
            throw new ProjectOwnerRequiredException(projectId);
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        String email = request.email().trim().toLowerCase();

        User userToAdd = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        boolean alreadyMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, userToAdd.getId());
        if (alreadyMember) {
            throw new ProjectMemberAlreadyExistsException(projectId, email);
        }

        ProjectMember member = new ProjectMember();
        member.setId(UUID.randomUUID());
        member.setProject(project);
        member.setUser(userToAdd);
        member.setRole(ProjectRole.MEMBER);
        member.setCreatedAt(OffsetDateTime.now());

        ProjectMember savedMember = projectMemberRepository.save(member);

        return toProjectMemberResponse(savedMember);
    }

    private void ensureProjectMember(UUID projectId, UUID userId) {
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
        if (!isMember) {
            throw new ProjectAccessDeniedException(projectId);
        }
    }

    private void ensureProjectExists(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }
    }

    private ProjectResponse toProjectResponse(Project project) {
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

    private ProjectMemberResponse toProjectMemberResponse(ProjectMember projectMember) {
        return new ProjectMemberResponse(
                projectMember.getId(),
                projectMember.getUser().getId(),
                projectMember.getUser().getEmail(),
                projectMember.getRole().name(),
                projectMember.getCreatedAt()
        );
    }
}