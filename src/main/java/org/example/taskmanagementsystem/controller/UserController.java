package org.example.taskmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.exception.UserNotFoundException;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.UserDto;
import org.example.taskmanagementsystem.service.TaskService;
import org.example.taskmanagementsystem.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<?> listUsers(@AuthenticationPrincipal User user,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "12") int size) {

        Page<UserDto> users = userService.getUsersPage(page, size);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal User user,
                                       @PathVariable Long userId) throws UserNotFoundException {
        UserDto userProfile = userService.getUserProfile(userId);

        return ResponseEntity.ok(userProfile);
    }


}

