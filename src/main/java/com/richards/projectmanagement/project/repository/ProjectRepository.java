package com.richards.projectmanagement.project.repository;

import com.richards.projectmanagement.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("""
            select p
            from Project p
            join ProjectMember pm on pm.project.id = p.id
            where pm.user.id = :userId
            order by p.createdAt desc
            """)
    List<Project> findAllByMemberUserId(UUID userId);
}