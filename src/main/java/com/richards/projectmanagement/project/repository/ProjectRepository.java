package com.richards.projectmanagement.project.repository;

import com.richards.projectmanagement.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
}