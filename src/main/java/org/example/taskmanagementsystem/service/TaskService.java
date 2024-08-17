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
import org.example.taskmanagementsystem.specification.TaskSpecification;
import org.example.taskmanagementsystem.util.TaskMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;


/**
 * Сервис для работы с задачами.
 * Предоставляет методы для создания, обновления, удаления и получения задач,
 * а также для фильтрации задач по различным критериям.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    /**
     * Получает страницу с задачами с учетом фильтрации и сортировки.
     *
     * @param reqUserId идентификатор пользователя, запросившего задачи (может быть null)
     * @param currentUserId идентификатор текущего пользователя
     * @param page номер страницы
     * @param size размер страницы
     * @param status статус задачи
     * @param priority приоритет задачи
     * @param filterType тип фильтрации задач
     * @return страница с задачами в формате DTO
     * @throws UserNotFoundException если пользователь не найден
     */
    public Page<ResponseTaskDto> getTaskPage(Long reqUserId, Long currentUserId, int page, int size, TaskStatus status, TaskPriority priority, TaskFilterType filterType) throws UserNotFoundException {

        Long userId = reqUserId != null ? reqUserId : currentUserId;
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Specification<Task> specification = TaskSpecification.filterTasks(userId, status, priority, filterType);
        Page<Task> taskPage = taskRepository.findAll(specification, pageable);
        return taskPage.map(taskMapper::toDto);
    }

    /**
     * Получает задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return задача в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     */
    public ResponseTaskDto getById(Long id) throws TaskNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return taskMapper.toDto(task);
    }

    /**
     * Сохраняет новую задачу.
     *
     * @param createTaskDto данные для создания задачи
     * @param user пользователь, создающий задачу
     * @return сохраненная задача в формате DTO
     * @throws AssigneeNotFoundException если исполнитель задачи не найден
     */
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

        return taskMapper.toDto(save);
    }

    /**
     * Обновляет существующую задачу.
     *
     * @param id идентификатор задачи
     * @param createTaskDto данные для обновления задачи
     * @param user пользователь, обновляющий задачу
     * @return обновленная задача в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     * @throws AssigneeNotFoundException если исполнитель задачи не найден
     * @throws AccessErrorException если у пользователя нет прав на обновление задачи
     */
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

    /**
     * Обновляет все поля задачи.
     *
     * @param task задача для обновления
     * @param createTaskDto данные для обновления задачи
     * @return обновленная задача в формате DTO
     * @throws AssigneeNotFoundException если исполнитель задачи не найден
     */
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

        return taskMapper.toDto(updatedTask);
    }

    /**
     * Обновляет статус задачи.
     *
     * @param task задача для обновления
     * @param createTaskDto данные для обновления задачи
     * @return обновленная задача в формате DTO
     */
    private ResponseTaskDto updateTaskStatus(Task task, CreateTaskDto createTaskDto) {
        task.setStatus(createTaskDto.getStatus());
        task.setUpdated(Instant.now());
        Task updatedTask = taskRepository.save(task);
        log.info("Task with ID {} has been status updated by user with ID {}", updatedTask.getId(), updatedTask.getAssignee().getId());
        return taskMapper.toDto(updatedTask);
    }

    /**
     * Удаляет задачу.
     *
     * @param taskId идентификатор задачи
     * @param user пользователь, удаляющий задачу
     * @throws TaskNotFoundException если задача не найдена
     * @throws AccessErrorException если у пользователя нет прав на удаление задачи
     */
    @Transactional
    public void delete(Long taskId, User user) throws TaskNotFoundException, AccessErrorException {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found"));
        if (!task.getAuthor().equals(user)) {
            throw new AccessErrorException("You do not have permission to delete this task");
        }
        log.info("Deleting task with id: {}", taskId);
        taskRepository.delete(task);
        log.info("Task with id {} deleted successfully", taskId);
    }

}
