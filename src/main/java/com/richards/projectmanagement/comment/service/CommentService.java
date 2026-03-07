package com.richards.projectmanagement.comment.service;

import com.richards.projectmanagement.comment.domain.Comment;
import com.richards.projectmanagement.comment.dto.CommentResponse;
import com.richards.projectmanagement.comment.dto.CreateCommentRequest;
import com.richards.projectmanagement.comment.dto.UpdateCommentRequest;
import com.richards.projectmanagement.comment.repository.CommentRepository;
import com.richards.projectmanagement.common.exception.CommentAccessDeniedException;
import com.richards.projectmanagement.common.exception.CommentNotFoundException;
import com.richards.projectmanagement.common.exception.CommentOwnershipRequiredException;
import com.richards.projectmanagement.common.exception.TaskNotFoundException;
import com.richards.projectmanagement.project.repository.ProjectMemberRepository;
import com.richards.projectmanagement.task.domain.Task;
import com.richards.projectmanagement.task.repository.TaskRepository;
import com.richards.projectmanagement.user.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public CommentService(
            CommentRepository commentRepository,
            TaskRepository taskRepository,
            ProjectMemberRepository projectMemberRepository
    ) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional
    public CommentResponse createComment(
            UUID taskId,
            CreateCommentRequest request,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        ensureProjectMember(task.getProject().getId(), currentUser.getId(), taskId);

        OffsetDateTime now = OffsetDateTime.now();

        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setTask(task);
        comment.setAuthor(currentUser);
        comment.setContent(request.content().trim());
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);

        Comment savedComment = commentRepository.save(comment);

        return toResponse(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByTask(
            UUID taskId,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        ensureProjectMember(task.getProject().getId(), currentUser.getId(), taskId);

        return commentRepository.findAllByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CommentResponse updateComment(
            UUID commentId,
            UpdateCommentRequest request,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        UUID projectId = comment.getTask().getProject().getId();

        ensureProjectMember(projectId, currentUser.getId(), comment.getTask().getId());

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new CommentOwnershipRequiredException(commentId);
        }

        comment.setContent(request.content().trim());
        comment.setUpdatedAt(OffsetDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return toResponse(savedComment);
    }

    @Transactional
    public void deleteComment(
            UUID commentId,
            Authentication authentication
    ) {
        User currentUser = (User) authentication.getPrincipal();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        UUID projectId = comment.getTask().getProject().getId();

        ensureProjectMember(projectId, currentUser.getId(), comment.getTask().getId());

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new CommentOwnershipRequiredException(commentId);
        }

        commentRepository.delete(comment);
    }

    private void ensureProjectMember(UUID projectId, UUID userId, UUID taskId) {
        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
        if (!isMember) {
            throw new CommentAccessDeniedException(taskId);
        }
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getEmail(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}