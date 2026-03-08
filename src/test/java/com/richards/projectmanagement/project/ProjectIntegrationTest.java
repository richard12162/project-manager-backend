package com.richards.projectmanagement.project;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richards.projectmanagement.auth.dto.LoginRequest;
import com.richards.projectmanagement.auth.dto.RegisterRequest;
import com.richards.projectmanagement.project.dto.CreateProjectRequest;
import com.richards.projectmanagement.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjectIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createProject_shouldReturn201_whenUserIsAuthenticated() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(
                "project-owner@example.com",
                "password123"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest(
                "project-owner@example.com",
                "password123"
        );

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        String token = jsonNode.get("token").asText();

        CreateProjectRequest request = new CreateProjectRequest(
                "My Test Project",
                "Integration test project"
        );

        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("My Test Project"))
                .andExpect(jsonPath("$.description").value("Integration test project"))
                .andExpect(jsonPath("$.ownerEmail").value("project-owner@example.com"));
    }

    @Test
    void createProject_shouldReturn401_whenUserIsNotAuthenticated() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest(
                "Unauthorized Project",
                "Should fail"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}