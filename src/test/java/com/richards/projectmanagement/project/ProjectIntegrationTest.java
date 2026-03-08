package com.richards.projectmanagement.project;

import com.fasterxml.jackson.databind.ObjectMapper;
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

        String email = uniqueEmail("project-owner");
        String token = registerAndLogin(email, "password123");

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
                .andExpect(jsonPath("$.ownerEmail").value(email));
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

    @Test
    void createProject_shouldReturn400_whenNameIsMissing() throws Exception {

        String token = registerAndLogin(uniqueEmail("validation-project"), "password123");

        String invalidProjectJson = """
        {
          "description": "Project without name"
        }
        """;

        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidProjectJson))
                .andExpect(status().isBadRequest());
    }
}