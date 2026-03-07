package com.richards.projectmanagement.task.service;

import com.richards.projectmanagement.common.exception.InvalidTaskAssigneeException;
import com.richards.projectmanagement.common.exception.ProjectNotFoundException;
import com.richards.projectmanagement.common.exception.TaskAccessDeniedException;
import com.richards.projectmanagement.common.exception.TaskNotFoundException;
import com.richards.projectmanagement.project.domain.Project;
import com.richards.projectmanagement.project.repository.ProjectMemberRepository;
import com.richards.projectmanagement.project.repository.ProjectRepository;
import com.richards.projectmanagement.task.domain.Task;
import com.richards.projectmanagement.task.domain.TaskPriority;
import com.richards.projectmanagement.task.domain.TaskStatus;
import com.richards.projectmanagement.task.dto.CreateTaskRequest;
import com.richards.projectmanagement.task.dto.TaskResponse;
import com.richards.projectmanagement.task.dto.UpdateTaskAssignmentRequest;
import com.richards.projectmanagement.task.dto.UpdateTaskRequest;
import com.richards.projectmanagement.task.dto.UpdateTaskStatusRequest;
import com.richards.projectmanagement.task.repository.TaskRepository;
import com.richards.projectmanagement.user.domain.User;
import com.richards.projectmanagement.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TaskResponse createTask(
            UUID projectId,
            CreateTaskRequest request,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        ensureProjectMember(projectId, currentUser.getId());

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        User assignee = null;
        if (request.assigneeId() != null) {
            assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new InvalidTaskAssigneeException(request.assigneeId(), projectId));

            boolean assigneeIsMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, assignee.getId());
            if (!assigneeIsMember) {
                throw new InvalidTaskAssigneeException(assignee.getId(), projectId);
            }
        }

        OffsetDateTime now = OffsetDateTime.now();

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setProject(project);
        task.setTitle(request.title().trim());
        task.setDescription(request.description() != null ? request.description().trim() : null);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM);
        task.setAssignee(assignee);
        task.setDueDate(request.dueDate());
        task.setCreatedAt(now);
        task.setUpdatedAt(now);

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProject(UUID projectId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        ensureProjectMember(projectId, currentUser.getId());

        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }

        return taskRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TaskResponse updateTask(
            UUID taskId,
            UpdateTaskRequest request,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        Task task = findTaskOrThrow(taskId);
        UUID projectId = task.getProject().getId();

        ensureProjectMember(projectId, currentUser.getId());

        task.setTitle(request.title().trim());
        task.setDescription(request.description() != null ? request.description().trim() : null);
        task.setPriority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM);
        task.setDueDate(request.dueDate());
        task.setUpdatedAt(OffsetDateTime.now());

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTaskStatus(
            UUID taskId,
            UpdateTaskStatusRequest request,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        Task task = findTaskOrThrow(taskId);
        UUID projectId = task.getProject().getId();

        ensureProjectMember(projectId, currentUser.getId());

        task.setStatus(request.status());
        task.setUpdatedAt(OffsetDateTime.now());

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTaskAssignment(
            UUID taskId,
            UpdateTaskAssignmentRequest request,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        Task task = findTaskOrThrow(taskId);
        UUID projectId = task.getProject().getId();

        ensureProjectMember(projectId, currentUser.getId());

        User assignee = null;

        if (request.assigneeId() != null) {
            assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new InvalidTaskAssigneeException(request.assigneeId(), projectId));

            boolean assigneeIsMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, assignee.getId());
            if (!assigneeIsMember) {
                throw new InvalidTaskAssigneeException(assignee.getId(), projectId);
            }
        }

        task.setAssignee(assignee);
        task.setUpdatedAt(OffsetDateTime.now());

        Task savedTask = taskRepository.save(task);

        return toResponse(savedTask);
    }

    private void ensureProjectMember(UUID projectId, UUID userId) {
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
        if (!isMember) {
            throw new TaskAccessDeniedException(projectId);
        }
    }

    private Task findTaskOrThrow(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getPriority().name(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getEmail() : null,
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}