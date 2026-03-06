package com.richards.projectmanagement.auth.service;

import com.richards.projectmanagement.auth.dto.RegisterRequest;
import com.richards.projectmanagement.auth.dto.RegisterResponse;
import com.richards.projectmanagement.common.exception.EmailAlreadyInUseException;
import com.richards.projectmanagement.user.domain.User;
import com.richards.projectmanagement.user.domain.UserRole;
import com.richards.projectmanagement.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        String email = request.email().toLowerCase().trim();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User();
        UUID id = UUID.randomUUID();
        user.setId(id);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setRole(UserRole.USER);
        user.setUpdatedAt(OffsetDateTime.now());
        user.setCreatedAt(OffsetDateTime.now());
        userRepository.save(user);

        return new RegisterResponse(id, email, UserRole.USER, OffsetDateTime.now());
    }
}
