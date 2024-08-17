package org.example.taskmanagementsystem.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.AccessErrorException;
import org.example.taskmanagementsystem.exception.TaskCommentNotFoundException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.model.TaskComment;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.CreateTaskCommentDto;
import org.example.taskmanagementsystem.model.dto.ResponseTaskCommentDto;
import org.example.taskmanagementsystem.repository.TaskCommentRepository;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.example.taskmanagementsystem.util.TaskCommentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Сервис для управления комментариями к задачам.
 * Предоставляет методы для получения, создания и обновления комментариев к задачам.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final TaskCommentMapper taskCommentMapper;


    /**
     * Получает страницу комментариев для указанной задачи.
     *
     * @param taskId идентификатор задачи
     * @param page номер страницы
     * @param size размер страницы
     * @return страница с комментариями в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     */
    public Page<ResponseTaskCommentDto> getCommentPage(Long taskId, int page, int size) throws TaskNotFoundException {

        taskService.getById(taskId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskComment> taskCommentPage = taskCommentRepository.findByTaskId(taskId, pageable);
        return taskCommentPage.map(taskCommentMapper::toDto);
    }

    /**
     * Сохраняет новый комментарий к задаче.
     *
     * @param user пользователь, создающий комментарий
     * @param createTaskCommentDto данные для создания комментария
     * @return сохраненный комментарий в формате DTO
     * @throws TaskNotFoundException если задача не найдена
     */
    @Transactional
    public ResponseTaskCommentDto saveComment(User user, CreateTaskCommentDto createTaskCommentDto) throws TaskNotFoundException {

        Task task = taskRepository.findById(createTaskCommentDto.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        TaskComment taskComment = TaskComment.builder()
                .content(createTaskCommentDto.getContent())
                .createAt(Instant.now())
                .author(user)
                .task(task).build();
        TaskComment save = taskCommentRepository.save(taskComment);
        return taskCommentMapper.toDto(save);
    }

    /**
     * Обновляет существующий комментарий к задаче.
     *
     * @param id идентификатор комментария
     * @param user пользователь, обновляющий комментарий
     * @param createTaskCommentDto данные для обновления комментария
     * @return обновленный комментарий в формате DTO
     * @throws TaskCommentNotFoundException если комментарий не найден
     * @throws AccessErrorException если у пользователя нет прав на обновление комментария
     */
    @Transactional
    public ResponseTaskCommentDto updateComment(Long id, User user, CreateTaskCommentDto createTaskCommentDto) throws TaskCommentNotFoundException, AccessErrorException {

        TaskComment taskComment = taskCommentRepository.findById(id)
                .orElseThrow(() -> new TaskCommentNotFoundException("comment not found"));

        if (!taskComment.getAuthor().equals(user)) {
            throw new AccessErrorException("You do not have permission to update this task");
        }

        taskComment.setContent(createTaskCommentDto.getContent());
        taskComment.setUpdateAt(Instant.now());
        TaskComment update = taskCommentRepository.save(taskComment);
        return taskCommentMapper.toDto(update);

    }
}
