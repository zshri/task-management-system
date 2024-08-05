package org.example.taskmanagementsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.AssigneeNotFoundException;
import org.example.taskmanagementsystem.exception.TaskCommentNotFoundException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.CreateTaskCommentDto;
import org.example.taskmanagementsystem.model.dto.CreateTaskDto;
import org.example.taskmanagementsystem.model.dto.ResponseTaskCommentDto;
import org.example.taskmanagementsystem.model.dto.ResponseTaskDto;
import org.example.taskmanagementsystem.service.TaskCommentService;
import org.example.taskmanagementsystem.service.TaskService;
import org.example.taskmanagementsystem.service.UserService;
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
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final TaskCommentService taskCommentService;


    @GetMapping
    public ResponseEntity<Page<ResponseTaskCommentDto>> getCommentPage(@AuthenticationPrincipal User user,
                                                                       @RequestParam Long taskId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "12") int size) throws TaskNotFoundException {
        Page<ResponseTaskCommentDto> taskCommentDtoPage;
        taskCommentDtoPage = taskCommentService.getCommentPage(taskId, page, size);

        return ResponseEntity.ok(taskCommentDtoPage);
    }

    @PostMapping
    public ResponseEntity<ResponseTaskCommentDto> createComment(@Valid @RequestBody CreateTaskCommentDto createTaskCommentDto,
                                                                @AuthenticationPrincipal User user) throws TaskNotFoundException {
        return ResponseEntity.ok(taskCommentService.saveComment(user, createTaskCommentDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskCommentDto> updateComment(@PathVariable Long id,
                                                      @Valid @RequestBody CreateTaskCommentDto createTaskCommentDto,
                                                      @AuthenticationPrincipal User user) throws AccessDeniedException, TaskNotFoundException, AssigneeNotFoundException, TaskCommentNotFoundException {
        return ResponseEntity.ok(taskCommentService.updateComment(id, user, createTaskCommentDto));
    }
}
