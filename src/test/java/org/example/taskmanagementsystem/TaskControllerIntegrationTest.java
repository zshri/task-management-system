package org.example.taskmanagementsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.taskmanagementsystem.model.TaskPriority;
import org.example.taskmanagementsystem.model.TaskStatus;
import org.example.taskmanagementsystem.model.dto.CreateTaskDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;


@SpringBootTest
@DisplayName("Task Controller Integration Tests")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testGetAllTasks_SuccessForCurrentUser() throws Exception {

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(10)))
                .andExpect(jsonPath("$.content[1].id", is(9)));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testGetAllTasks_SuccessForAnyUser() throws Exception {

        mockMvc.perform(get("/api/v1/tasks")
                        .param("userId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(10)))
                .andExpect(jsonPath("$.content[1].id", is(9)));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testGetAllTasks_NotFoundUser() throws Exception {

        mockMvc.perform(get("/api/v1/tasks")
                        .param("userId", "3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("User not found")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testGetTaskById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Новая задача 1")));
    }
    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testGetTaskById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/11"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task not found")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testCreateTask_Success() throws Exception {

        CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .title("Тестовая задача")
                .description("Тестовая задача")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .assignee(1L)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(20)));
    }
    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testCreateTask_AssigneeNotFoundException() throws Exception {

        CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .title("Тестовая задача")
                .description("Тестовая задача")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .assignee(3L)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Assignee not found")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testCreateTask_ValidationException() throws Exception {

        CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .title("")
                .description("Тестовая задача")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .assignee(1L)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Validation failed")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testUpdateTask_Success() throws Exception {

        CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .title("Обновленная тестовая задача")
                .description("Тестовая задача")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .assignee(1L)
                .build();

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Обновленная тестовая задача")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testUpdateTask_TaskNotFound() throws Exception {

        CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .title("Обновленная тестовая задача")
                .description("Тестовая задача")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .assignee(1L)
                .build();

        mockMvc.perform(put("/api/v1/tasks/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task not found")));
    }
    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testUpdateTask_AssigneeNotFound() throws Exception {

        CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .title("Обновленная тестовая задача")
                .description("Тестовая задача")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .assignee(3L)
                .build();

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Assignee not found")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testUpdateTask_AccessError() throws Exception {

        CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .title("Обновленная тестовая задача")
                .description("Тестовая задача")
                .status(TaskStatus.PENDING)
                .priority(TaskPriority.MEDIUM)
                .assignee(1L)
                .build();

        mockMvc.perform(put("/api/v1/tasks/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDto)))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.message", is("You do not have permission to update this task")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testUpdateTask_AssigneeUpdateSuccess() throws Exception {

        CreateTaskDto createTaskDto = CreateTaskDto.builder()
                .title("Обновленная тестовая задача")
                .description("Тестовая задача")
                .status(TaskStatus.COMPLETED)
                .priority(TaskPriority.MEDIUM)
                .assignee(1L)
                .build();

        mockMvc.perform(put("/api/v1/tasks/9")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")))
                .andExpect(jsonPath("$.title", not("Обновленная тестовая задача")));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testDeleteTask_Success() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testDeleteTask_NotFound() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/11"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Task not found")));
    }
    @Test
    @Transactional
    @WithUserDetails(value = "jo22hrn.doe@example.com")
    public void testDeleteTask_AccessError() throws Exception {

        mockMvc.perform(delete("/api/v1/tasks/6"))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.message", is("You do not have permission to delete this task")));
    }
}