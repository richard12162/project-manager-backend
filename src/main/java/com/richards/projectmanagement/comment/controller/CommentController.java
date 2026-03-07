package com.richards.projectmanagement.comment.controller;

import com.richards.projectmanagement.comment.dto.CommentResponse;
import com.richards.projectmanagement.comment.dto.CreateCommentRequest;
import com.richards.projectmanagement.comment.dto.UpdateCommentRequest;
import com.richards.projectmanagement.comment.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID taskId,
            @RequestBody @Valid CreateCommentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.status(201).body(commentService.createComment(taskId, request, authentication));
    }

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByTask(
            @PathVariable UUID taskId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId, authentication));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable UUID commentId,
            @RequestBody @Valid UpdateCommentRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request, authentication));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID commentId,
            Authentication authentication
    ) {
        commentService.deleteComment(commentId, authentication);
        return ResponseEntity.noContent().build();
    }
}