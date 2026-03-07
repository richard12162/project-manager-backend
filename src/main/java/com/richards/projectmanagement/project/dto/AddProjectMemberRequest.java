package com.richards.projectmanagement.project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddProjectMemberRequest(

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email

) {
}