package com.richards.projectmanagement.project.repository;

import com.richards.projectmanagement.project.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, UUID> {
}