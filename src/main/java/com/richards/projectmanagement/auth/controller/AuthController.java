package com.richards.projectmanagement.auth.controller;

import com.richards.projectmanagement.auth.dto.AuthResponse;
import com.richards.projectmanagement.auth.dto.CurrentUserResponse;
import com.richards.projectmanagement.auth.dto.LoginRequest;
import com.richards.projectmanagement.auth.dto.RegisterRequest;
import com.richards.projectmanagement.auth.dto.RegisterResponse;
import com.richards.projectmanagement.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.status(201).body(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication));
    }
}