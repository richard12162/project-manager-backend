package com.richards.projectmanagement.comment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richards.projectmanagement.comment.dto.CreateCommentRequest;
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

class CommentIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createComment_shouldReturn201_whenUserIsProjectMember() throws Exception {
        String token = registerAndLogin(uniqueEmail("commenter"), "password123");
        String projectId = createProjectAndReturnId(token, "Comment Project", "Project for comments");
        String taskId = createTaskAndReturnId(token, projectId, "Task with comments");

        CreateCommentRequest request = new CreateCommentRequest("This is my first comment");

        mockMvc.perform(post("/tasks/" + taskId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.content").value("This is my first comment"));
    }

    @Test
    void createComment_shouldReturn401_whenUserIsNotAuthenticated() throws Exception {
        String token = registerAndLogin(uniqueEmail("comment-owner"), "password123");
        String projectId = createProjectAndReturnId(token, "Comment Auth Project", "Project for auth test");
        String taskId = createTaskAndReturnId(token, projectId, "Task with comments");

        CreateCommentRequest request = new CreateCommentRequest("Unauthorized comment");

        mockMvc.perform(post("/tasks/" + taskId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createComment_shouldReturn403_whenUserIsNotProjectMember() throws Exception {
        String ownerToken = registerAndLogin(uniqueEmail("owner"), "password123");
        String strangerToken = registerAndLogin(uniqueEmail("stranger"), "password123");

        String projectId = createProjectAndReturnId(ownerToken, "Private Comment Project", "Project is private");
        String taskId = createTaskAndReturnId(ownerToken, projectId, "Private task");

        CreateCommentRequest request = new CreateCommentRequest("I should not be allowed");

        mockMvc.perform(post("/tasks/" + taskId + "/comments")
                        .header("Authorization", "Bearer " + strangerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCommentsByTask_shouldReturn200_whenUserIsProjectMember() throws Exception {
        String token = registerAndLogin(uniqueEmail("reader-comments"), "password123");
        String projectId = createProjectAndReturnId(token, "Read Comments Project", "Project for reading comments");
        String taskId = createTaskAndReturnId(token, projectId, "Task with readable comments");

        CreateCommentRequest request = new CreateCommentRequest("Stored comment");

        mockMvc.perform(post("/tasks/" + taskId + "/comments")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks/" + taskId + "/comments")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].content").value("Stored comment"));
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

    private String createTaskAndReturnId(String token, String projectId, String title) throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(
                title,
                "Task description",
                TaskPriority.HIGH,
                null,
                null
        );

        String response = mockMvc.perform(post("/projects/" + projectId + "/tasks")
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