package com.richards.projectmanagement.auth.Controller;

import com.richards.projectmanagement.auth.dto.RegisterRequest;
import com.richards.projectmanagement.auth.dto.RegisterResponse;
import com.richards.projectmanagement.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
