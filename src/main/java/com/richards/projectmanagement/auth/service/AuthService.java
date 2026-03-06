package com.richards.projectmanagement.auth.service;

import com.richards.projectmanagement.auth.dto.AuthResponse;
import com.richards.projectmanagement.auth.dto.CurrentUserResponse;
import com.richards.projectmanagement.auth.dto.LoginRequest;
import com.richards.projectmanagement.auth.dto.RegisterRequest;
import com.richards.projectmanagement.auth.dto.RegisterResponse;
import com.richards.projectmanagement.common.exception.EmailAlreadyInUseException;
import com.richards.projectmanagement.common.exception.InvalidCredentialsException;
import com.richards.projectmanagement.user.domain.User;
import com.richards.projectmanagement.user.domain.UserRole;
import com.richards.projectmanagement.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public RegisterResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }

        OffsetDateTime now = OffsetDateTime.now();

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token, "Bearer");
    }

    public CurrentUserResponse getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        return new CurrentUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}