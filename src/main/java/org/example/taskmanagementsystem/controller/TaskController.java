package org.example.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.AssigneeNotFoundException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.model.*;
import org.example.taskmanagementsystem.model.dto.CreateTaskDto;
import org.example.taskmanagementsystem.model.dto.ResponseTaskDto;
import org.example.taskmanagementsystem.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;


@Slf4j
//@CrossOrigin(origins = "https://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;


    @GetMapping
    public ResponseEntity<Page<ResponseTaskDto>> getAllTasks(@AuthenticationPrincipal User user,
                                                             @RequestParam(required = false) Long userId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "12") int size,
                                                             @RequestParam(required = false) TaskStatus status,
                                                             @RequestParam(required = false) TaskPriority priority,
                                                             @RequestParam(defaultValue = "ALL") TaskFilterType filterType
    ) {
        Page<ResponseTaskDto> tasks;
        if (userId != null) {
            tasks = taskService.getTaskPage(userId, page, size, status, priority, filterType );
        } else {
            tasks = taskService.getTaskPage(user.getId(), page, size, status, priority, filterType );
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> getTaskById(@PathVariable Long id) throws TaskNotFoundException {
       return ResponseEntity.ok(taskService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ResponseTaskDto> createTask(@Valid @RequestBody CreateTaskDto createTaskDto,
                                                      @AuthenticationPrincipal User user) throws AssigneeNotFoundException {
        return ResponseEntity.ok(taskService.save(createTaskDto, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> updateTask(@PathVariable Long id,
                                                      @Valid @RequestBody CreateTaskDto createTaskDto,
                                                      @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException, AssigneeNotFoundException {
        return ResponseEntity.ok(taskService.update(id, createTaskDto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id,
                                        @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException {
        taskService.delete(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
