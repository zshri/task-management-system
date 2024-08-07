package org.example.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.AccessErrorException;
import org.example.taskmanagementsystem.exception.TaskCommentNotFoundException;
import org.example.taskmanagementsystem.exception.TaskNotFoundException;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.*;
import org.example.taskmanagementsystem.service.TaskCommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Comment Controller")
@SecurityRequirement(name = "JWT")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final TaskCommentService taskCommentService;


    @Operation(
            summary = "Получить страницу комментариев",
            description = "Извлекает постраничный список комментариев по id задачи отсортированых в обратном порядке по id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved the page of comments"
            ),
            @ApiResponse(responseCode = "404",
                    description = "Задача не найден",
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
    public ResponseEntity<Page<ResponseTaskCommentDto>> getCommentPage(@Parameter(description = "ID задачи", required = true)@RequestParam @Positive(message = "Task ID must be a positive number") Long taskId,
                                                                       @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
                                                                       @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "12") @Max(value = 50, message = "Page size max 50") int size) throws TaskNotFoundException {

        return ResponseEntity.ok(taskCommentService.getCommentPage(taskId, page, size));
    }

    @Operation(
            summary = "Создать комментарий",
            description = "Позволяет оставить комментарий к задаче."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно создан",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTaskCommentDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class)))
    })
    @PostMapping
    public ResponseEntity<ResponseTaskCommentDto> createComment(@Valid @RequestBody CreateTaskCommentDto createTaskCommentDto,
                                                                @AuthenticationPrincipal User user) throws TaskNotFoundException {
        return ResponseEntity.ok(taskCommentService.saveComment(user, createTaskCommentDto));
    }

    @Operation(
            summary = "Обновить комментарий",
            description = "Позволяет обновить комментарий к задаче."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseTaskDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "404", description = "Комментарий с заданым id не найден",
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
    public ResponseEntity<ResponseTaskCommentDto> updateComment(@Parameter(description = "ID комментария", required = true)@PathVariable @Positive(message = "ID must be positive") Long id,
                                                      @Valid @RequestBody CreateTaskCommentDto createTaskCommentDto,
                                                      @AuthenticationPrincipal User user) throws TaskCommentNotFoundException, AccessErrorException {
        return ResponseEntity.ok(taskCommentService.updateComment(id, user, createTaskCommentDto));
    }
}