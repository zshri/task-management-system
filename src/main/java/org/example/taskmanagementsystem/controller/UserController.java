package org.example.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.UserNotFoundException;
import org.example.taskmanagementsystem.model.dto.ResponseError;
import org.example.taskmanagementsystem.model.dto.ResponseUserDto;
import org.example.taskmanagementsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Tag(name="User Controller")
@SecurityRequirement(name = "JWT")
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Получить страницу пользователей",
            description = "Позволяет получить страницу с пользователями"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Страница пользователей получена"),
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(implementation = ResponseUserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ResponseUserDto>> listUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "12") int size) {

        return ResponseEntity.ok(userService.getUsersPage(page, size));
    }

    @Operation(
            summary = "Получить пользователя по id",
            description = "Позволяет получить пользователя по id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Профиль пользователя получен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ResponseUserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseError.class)))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUserDto> getUserProfile(@Parameter(description = "ID пользователя", required = true)@PathVariable @Positive(message = "ID must be positive") Long userId) throws UserNotFoundException {

        return ResponseEntity.ok(userService.getUserProfile(userId));
    }


}

