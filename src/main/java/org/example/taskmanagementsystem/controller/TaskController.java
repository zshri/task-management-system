package org.example.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.AccessErrorException;
import org.example.taskmanagementsystem.exception.AssigneeNotFoundException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.exception.UserNotFoundException;
import org.example.taskmanagementsystem.model.*;
import org.example.taskmanagementsystem.model.dto.CreateTaskDto;
import org.example.taskmanagementsystem.model.dto.ResponseError;
import org.example.taskmanagementsystem.model.dto.ResponseTaskDto;
import org.example.taskmanagementsystem.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name="Task Controller")
@SecurityRequirement(name = "JWT")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Получить все задачи пользователя или другого пользователя",
            description = "Извлекает постраничный список задач на основе предоставленных фильтров и идентификатора пользователя."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the list of tasks"
//                    content = @Content(
//                            schema = @Schema(implementation = Page.class, subTypes = {ResponseTaskDto.class}),
//                            mediaType = "application/json"
//                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ResponseTaskDto>> getAllTasks(@AuthenticationPrincipal User user,
                                                             @Parameter(
                                                                     name = "userId",
                                                                     description = "ID пользователя для фильтрации задач. Если не указано, используется ID аутентифицированного пользователя.",
                                                                     in = ParameterIn.QUERY,
                                                                     schema = @Schema(type = "integer", format = "int64")
                                                             )@RequestParam(required = false) Long userId,
                                                             @Parameter(
                                                                     name = "page",
                                                                     description = "Номер страницы для извлечения.",
                                                                     in = ParameterIn.QUERY,
                                                                     schema = @Schema(type = "integer", format = "int32", defaultValue = "0")
                                                             )@RequestParam(defaultValue = "0") int page,
                                                             @Parameter(
                                                                     name = "size",
                                                                     description = "Количество задач на странице.",
                                                                     in = ParameterIn.QUERY,
                                                                     schema = @Schema(type = "integer", format = "int32", defaultValue = "12")
                                                             )@RequestParam(defaultValue = "12") int size,
                                                             @Parameter(
                                                                     name = "status",
                                                                     description = "Фильтровать задачи по статусу.",
                                                                     in = ParameterIn.QUERY,
                                                                     schema = @Schema(implementation = TaskStatus.class)
                                                             )@RequestParam(required = false) TaskStatus status,
                                                             @Parameter(
                                                                     name = "priority",
                                                                     description = "Фильтровать задачи по приоритету.",
                                                                     in = ParameterIn.QUERY,
                                                                     schema = @Schema(implementation = TaskPriority.class)
                                                             )@RequestParam(required = false) TaskPriority priority,
                                                             @Parameter(
                                                                     name = "filterType",
                                                                     description = "Тип фильтра для применения (AUTHOR, ASSIGNEE, ALL).",
                                                                     in = ParameterIn.QUERY,
                                                                     schema = @Schema(implementation = TaskFilterType.class, defaultValue = "ALL")
                                                             )@RequestParam(defaultValue = "ALL") TaskFilterType filterType) throws UserNotFoundException {

        return ResponseEntity.ok(taskService.getTaskPage(userId, user.getId(), page, size, status, priority, filterType ));
    }

    @Operation(
            summary = "Получить задачу по id",
            description = "Позволяет получить задачу по id без коментариев к ней."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получена задача",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTaskDto.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> getTaskById(@Parameter(description = "ID задачи", required = true)@PathVariable @Positive(message = "ID must be positive") Long id) throws Exception {

       return ResponseEntity.ok(taskService.getById(id));
    }

    @Operation(
            summary = "Создать задачу",
            description = "Позволяет создать задачу."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно создана",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "Исполнитель с заданым id не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseTaskDto> createTask(@Valid @RequestBody CreateTaskDto createTaskDto,
                                                      @AuthenticationPrincipal User user) throws AssigneeNotFoundException {
        return ResponseEntity.ok(taskService.save(createTaskDto, user));
    }

    @Operation(
            summary = "Обновить задачу",
            description = "Позволяет обновить задачу."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "Задача или исполнитель с заданым id не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "423", description = "Доступ закрыт",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponseTaskDto> updateTask(@Parameter(description = "ID задачи", required = true)@PathVariable @Positive(message = "ID must be positive") Long id,
                                                      @Valid @RequestBody CreateTaskDto createTaskDto,
                                                      @AuthenticationPrincipal User user) throws TaskNotFoundException, AssigneeNotFoundException, AccessErrorException {
        return ResponseEntity.ok(taskService.update(id, createTaskDto, user));
    }

    @Operation(
            summary = "Удалить задачу",
            description = "Позволяет удалить задачу."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "423", description = "Доступ закрыт",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@Parameter(description = "ID задачи", required = true)@PathVariable @Positive(message = "ID must be positive") Long id,
                                        @AuthenticationPrincipal User user) throws TaskNotFoundException, AccessErrorException {
        taskService.delete(id, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
