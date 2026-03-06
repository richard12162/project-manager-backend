package com.richards.projectmanagement.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import com.richards.projectmanagement.user.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    public User findByEmail(String email);

    public boolean existsByEmail(String email);
}
