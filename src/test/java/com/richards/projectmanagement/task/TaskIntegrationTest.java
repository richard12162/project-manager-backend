package com.richards.projectmanagement.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richards.projectmanagement.project.dto.CreateProjectRequest;
import com.richards.projectmanagement.support.AbstractIntegrationTest;
import com.richards.projectmanagement.task.domain.TaskPriority;
import com.richards.projectmanagement.task.dto.CreateTaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createTask_shouldReturn201_whenUserIsProjectMember() throws Exception {
        String token = registerAndLogin(uniqueEmail("task-owner"), "password123");
        String projectId = createProjectAndReturnId(token, "Task Test Project", "Project for task tests");

        CreateTaskRequest request = new CreateTaskRequest(
                "Implement login endpoint",
                "Add JWT login flow",
                TaskPriority.HIGH,
                null,
                null
        );

        mockMvc.perform(post("/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Implement login endpoint"))
                .andExpect(jsonPath("$.description").value("Add JWT login flow"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void createTask_shouldReturn401_whenUserIsNotAuthenticated() throws Exception {
        String token = registerAndLogin(uniqueEmail("project-owner"), "password123");
        String projectId = createProjectAndReturnId(token, "Unauthorized Task Project", "Should fail without auth");

        CreateTaskRequest request = new CreateTaskRequest(
                "Implement login endpoint",
                "Add JWT login flow",
                TaskPriority.HIGH,
                null,
                null
        );

        mockMvc.perform(post("/projects/" + projectId + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createTask_shouldReturn403_whenUserIsNotProjectMember() throws Exception {
        String ownerToken = registerAndLogin(uniqueEmail("owner"), "password123");
        String strangerToken = registerAndLogin(uniqueEmail("stranger"), "password123");

        String projectId = createProjectAndReturnId(ownerToken, "Private Project", "Only owner should access");

        CreateTaskRequest request = new CreateTaskRequest(
                "Implement login endpoint",
                "Add JWT login flow",
                TaskPriority.HIGH,
                null,
                null
        );

        mockMvc.perform(post("/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + strangerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTasksByProject_shouldReturn200_whenUserIsProjectMember() throws Exception {
        String token = registerAndLogin(uniqueEmail("reader"), "password123");
        String projectId = createProjectAndReturnId(token, "Read Tasks Project", "Project for reading tasks");

        CreateTaskRequest request = new CreateTaskRequest(
                "Implement login endpoint",
                "Add JWT login flow",
                TaskPriority.HIGH,
                null,
                null
        );

        mockMvc.perform(post("/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/projects/" + projectId + "/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].title").value("Implement login endpoint"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.page").value(0));
    }

    private String createProjectAndReturnId(String token, String name, String description) throws Exception {
        CreateProjectRequest request = new CreateProjectRequest(name, description);

        String response = mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("id").asText();
    }
}