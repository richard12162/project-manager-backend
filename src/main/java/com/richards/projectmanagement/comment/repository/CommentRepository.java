package com.richards.projectmanagement.comment.repository;

import com.richards.projectmanagement.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByTaskIdOrderByCreatedAtAsc(UUID taskId);
}