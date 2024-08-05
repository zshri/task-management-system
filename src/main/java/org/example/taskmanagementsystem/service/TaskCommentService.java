package org.example.taskmanagementsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.TaskCommentNotFoundException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.model.TaskComment;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.CreateTaskCommentDto;
import org.example.taskmanagementsystem.model.dto.ResponseTaskCommentDto;
import org.example.taskmanagementsystem.repository.TaskCommentRepository;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final UserService userService;
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public Page<ResponseTaskCommentDto> getCommentPage(Long taskId, int page, int size) throws TaskNotFoundException {

        taskService.getById(taskId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskComment> taskCommentPage = taskCommentRepository.findByTaskId(taskId, pageable);
        return taskCommentPage.map(this::convertToDto);
    }

    private ResponseTaskCommentDto convertToDto(TaskComment taskComment) {
        ResponseTaskCommentDto taskCommentDto = new ResponseTaskCommentDto();
        taskCommentDto.setId(taskComment.getId());
        taskCommentDto.setContent(taskComment.getContent());
        taskCommentDto.setCreateAt(taskComment.getCreateAt());
        taskCommentDto.setUpdateAt(taskComment.getUpdateAt());
        taskCommentDto.setAuthor(userService.convertToDto(taskComment.getAuthor()));
        return taskCommentDto;
    }

    public ResponseTaskCommentDto saveComment(User user, CreateTaskCommentDto createTaskCommentDto) throws TaskNotFoundException {

        Task task = taskRepository.findById(createTaskCommentDto.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        TaskComment taskComment = TaskComment.builder()
                .content(createTaskCommentDto.getContent())
                .createAt(Instant.now())
                .author(user)
                .task(task).build();
        TaskComment save = taskCommentRepository.save(taskComment);
        return convertToDto(save);
    }

    public ResponseTaskCommentDto updateComment(Long id, User user, CreateTaskCommentDto createTaskCommentDto) throws TaskNotFoundException, TaskCommentNotFoundException, AccessDeniedException {

        TaskComment taskComment = taskCommentRepository.findById(id)
                .orElseThrow(() -> new TaskCommentNotFoundException("comment not found"));

        if (!taskComment.getAuthor().equals(user)) {
            throw new AccessDeniedException("access denied");
        }

        taskComment.setContent(createTaskCommentDto.getContent());
        taskComment.setUpdateAt(Instant.now());
        TaskComment update = taskCommentRepository.save(taskComment);
        return convertToDto(update);

    }
}
