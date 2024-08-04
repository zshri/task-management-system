package org.example.taskmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.service.TaskService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
//@CrossOrigin(origins = "https://localhost:3000")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    //    @GetMapping
//    public List<Task> getUserTasks(@AuthenticationPrincipal User user) {
//
//    }
//

    @GetMapping("/a")
    public String get(@AuthenticationPrincipal User user) {
        System.out.println(user.getId());


        return "Hello World" + user.getEmail();
    }
//
//    @GetMapping("/{taskId}")
//    public Task getTask(@PathVariable String taskId) {
//
//    }
//
//    @PostMapping("/add")
//    public Task addTask(@RequestBody RequestTaskDto requestTaskDto,
//                        @AuthenticationPrincipal User user) {
//
//
//        return task;
//    }

}
