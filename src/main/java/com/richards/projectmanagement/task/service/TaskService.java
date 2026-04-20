package com.richards.projectmanagement.task.service;

import com.richards.projectmanagement.activity.domain.ActivityType;
import com.richards.projectmanagement.activity.service.ActivityLogService;
import com.richards.projectmanagement.common.dto.PagedResponse;
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
import com.richards.projectmanagement.task.dto.*;
import com.richards.projectmanagement.task.repository.TaskRepository;
import com.richards.projectmanagement.task.repository.TaskSpecifications;
import com.richards.projectmanagement.user.domain.User;
import com.richards.projectmanagement.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            UserRepository userRepository,
            ActivityLogService activityLogService
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
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

        activityLogService.log(
                project,
                currentUser,
                ActivityType.TASK_CREATED,
                savedTask.getId(),
                "TASK",
                "Created task: " + savedTask.getTitle()
        );

        return toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public PagedResponse<TaskResponse> getTasksByProject(
            UUID projectId,
            TaskStatus status,
            TaskPriority priority,
            UUID assigneeId,
            int page,
            int size,
            String sort,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        ensureProjectMember(projectId, currentUser.getId());

        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException(projectId);
        }

        Sort sortSpec = buildSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);

        Specification<Task> specification = Specification
                .where(TaskSpecifications.hasProjectId(projectId))
                .and(TaskSpecifications.hasStatus(status))
                .and(TaskSpecifications.hasPriority(priority))
                .and(TaskSpecifications.hasAssigneeId(assigneeId));

        Page<Task> tasksPage = taskRepository.findAll(specification, pageable);

        return new PagedResponse<>(
                tasksPage.getContent().stream().map(this::toResponse).toList(),
                tasksPage.getNumber(),
                tasksPage.getSize(),
                tasksPage.getTotalElements(),
                tasksPage.getTotalPages(),
                tasksPage.isFirst(),
                tasksPage.isLast()
        );
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

        TaskStatus oldStatus = task.getStatus();

        task.setStatus(request.status());
        task.setUpdatedAt(OffsetDateTime.now());

        Task savedTask = taskRepository.save(task);

        if (oldStatus != request.status()) {
            activityLogService.log(
                    task.getProject(),
                    currentUser,
                    ActivityType.TASK_STATUS_CHANGED,
                    savedTask.getId(),
                    "TASK",
                    "Changed task status from " + oldStatus.name() + " to " + request.status().name()
            );
        }

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

        User previousAssignee = task.getAssignee();
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

        String oldAssigneeText = previousAssignee != null ? previousAssignee.getEmail() : "unassigned";
        String newAssigneeText = assignee != null ? assignee.getEmail() : "unassigned";

        if (!oldAssigneeText.equals(newAssigneeText)) {
            activityLogService.log(
                    task.getProject(),
                    currentUser,
                    ActivityType.TASK_ASSIGNEE_CHANGED,
                    savedTask.getId(),
                    "TASK",
                    "Changed task assignee from " + oldAssigneeText + " to " + newAssigneeText
            );
        }

        return toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getMyTasks(
            Authentication authentication
    )
        {
        User currentUser = (User) authentication.getPrincipal();
        return taskRepository.findAllByAssigneeId(currentUser.getId()).stream().map(this::toResponse).toList();
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

    private Sort buildSort(String sort) {
        return switch (sort) {
            case "createdAtAsc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "dueDateAsc" -> Sort.by(Sort.Direction.ASC, "dueDate");
            case "dueDateDesc" -> Sort.by(Sort.Direction.DESC, "dueDate");
            case "priorityAsc" -> Sort.by(Sort.Direction.ASC, "priority");
            case "priorityDesc" -> Sort.by(Sort.Direction.DESC, "priority");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getProject().getName(),
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
