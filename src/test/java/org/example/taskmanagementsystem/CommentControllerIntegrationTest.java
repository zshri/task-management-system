package org.example.taskmanagementsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.taskmanagementsystem.model.dto.CreateTaskCommentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("Comment Controller Integration Tests")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();

        jdbcTemplate.execute("ALTER SEQUENCE public.comments_id_seq RESTART WITH 20");
    }



    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testGetCommentPage_Success() throws Exception {

        mockMvc.perform(get("/api/v1/comments").param("taskId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(2)));

    }
    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testGetCommentPage_TaskNotFound() throws Exception {

        mockMvc.perform(get("/api/v1/comments").param("taskId", "12"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task not found")));

    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testCreateComment_Success() throws Exception {

        CreateTaskCommentDto createTaskCommentDto = CreateTaskCommentDto.builder()
                .taskId(1L)
                .content("Тестовый комментарий")
                .build();

        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(20)))
                .andExpect(jsonPath("$.content", is("Тестовый комментарий")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testCreateComment_ValidationException() throws Exception {

        CreateTaskCommentDto createTaskCommentDto = CreateTaskCommentDto.builder()
                .taskId(1L)
                .content("")
                .build();

        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskCommentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")));
    }

}
