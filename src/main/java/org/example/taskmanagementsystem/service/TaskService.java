package org.example.taskmanagementsystem.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.AccessErrorException;
import org.example.taskmanagementsystem.exception.AssigneeNotFoundException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.exception.UserNotFoundException;
import org.example.taskmanagementsystem.model.*;
import org.example.taskmanagementsystem.model.dto.CreateTaskDto;
import org.example.taskmanagementsystem.model.dto.ResponseTaskDto;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.example.taskmanagementsystem.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public Page<ResponseTaskDto> getTaskPage(Long reqUserId, Long currentUserId, int page, int size, TaskStatus status, TaskPriority priority, TaskFilterType filterType) throws UserNotFoundException {

        Long userId;

        if (reqUserId != null) {
            userId = reqUserId;
        } else {
            userId = currentUserId;
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Specification<Task> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            switch (filterType) {
                case AUTHOR -> predicates.add(criteriaBuilder.equal(root.get("author").get("id"), userId));
                case ASSIGNEE -> predicates.add(criteriaBuilder.equal(root.get("assignee").get("id"), userId));
                case ALL -> predicates.add(criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("author").get("id"), userId),
                            criteriaBuilder.equal(root.get("assignee").get("id"), userId)));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Task> taskPage = taskRepository.findAll(specification, pageable);
        return taskPage.map(this::convertToDto);
    }

    public ResponseTaskDto getById(Long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return convertToDto(task);
    }

    @Transactional
    public ResponseTaskDto save(CreateTaskDto createTaskDto, User user) throws AssigneeNotFoundException {

        User assigneeById = null;

        if (createTaskDto.getAssignee() != null) {
            assigneeById = userRepository.findById(createTaskDto.getAssignee())
                    .orElseThrow(() -> new AssigneeNotFoundException("Assignee not found"));
        }

        Task task = Task.builder()
                .title(createTaskDto.getTitle())
                .description(createTaskDto.getDescription())
                .status(createTaskDto.getStatus())
                .priority(createTaskDto.getPriority())
                .author(user)
                .assignee(assigneeById)
                .created(Instant.now())
                .build();

        Task save = taskRepository.save(task);
        log.info("Task with id {} save", save.getId());

        return convertToDto(save);
    }

    @Transactional
    public ResponseTaskDto update(Long id, CreateTaskDto createTaskDto, User user) throws TaskNotFoundException, AssigneeNotFoundException, AccessErrorException {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        if (task.getAuthor().getId().equals(user.getId())) {
            log.info("User with ID {} is updating task with ID {} as the author", user.getId(), id);
            return updateTask(task, createTaskDto);
        } else if (task.getAssignee().getId().equals(user.getId())) {
            log.info("User with ID {} is updating task with ID {} as the assignee", user.getId(), id);
            return updateTaskStatus(task, createTaskDto);
        } else {
            throw new AccessErrorException("You do not have permission to update this task");
        }
    }

    private ResponseTaskDto updateTask(Task task, CreateTaskDto createTaskDto) throws AssigneeNotFoundException {

        if (createTaskDto.getAssignee() != null) {
            User assigneeById = userRepository.findById(createTaskDto.getAssignee())
                    .orElseThrow(() -> new AssigneeNotFoundException("Assignee not found"));
            task.setAssignee(assigneeById);
        } else {
            task.setAssignee(null);
        }
        task.setTitle(createTaskDto.getTitle());
        task.setDescription(createTaskDto.getDescription());
        task.setStatus(createTaskDto.getStatus());
        task.setPriority(createTaskDto.getPriority());
        task.setUpdated(Instant.now());
        Task updatedTask = taskRepository.save(task);
        log.info("Task with ID {} has been fully updated by user with ID {}", updatedTask.getId(), updatedTask.getAuthor().getId());

        return convertToDto(updatedTask);
    }

    private ResponseTaskDto updateTaskStatus(Task task, CreateTaskDto createTaskDto) {
        task.setStatus(createTaskDto.getStatus());
        task.setUpdated(Instant.now());

        Task updatedTask = taskRepository.save(task);
        log.info("Task with ID {} has been status updated by user with ID {}", updatedTask.getId(), updatedTask.getAssignee().getId());
        return convertToDto(updatedTask);
    }

    @Transactional
    public void delete(Long taskId, User user) throws TaskNotFoundException, AccessErrorException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (!task.getAuthor().equals(user)) {
            throw new AccessErrorException("You do not have permission to delete this task");
        }
        log.info("Deleting task with id: {}", taskId);
        taskRepository.delete(task);
        log.info("Task with id {} deleted successfully", taskId);
    }

    private ResponseTaskDto convertToDto(Task task) {
        ResponseTaskDto taskDto = ResponseTaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .author(userService.convertToDto(task.getAuthor()))
                .assignee(task.getAssignee() != null ? userService.convertToDto(task.getAssignee()) : null)
                .created(task.getCreated())
                .updated(task.getUpdated())
                .build();
        return taskDto;
    }

}
