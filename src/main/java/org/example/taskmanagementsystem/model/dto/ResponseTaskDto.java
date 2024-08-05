package org.example.taskmanagementsystem.model.dto;

import lombok.*;
import org.example.taskmanagementsystem.model.TaskPriority;
import org.example.taskmanagementsystem.model.TaskStatus;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseTaskDto {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private ResponseUserDto author;
    private ResponseUserDto assignee;
    private Instant created;
    private Instant updated;

}
