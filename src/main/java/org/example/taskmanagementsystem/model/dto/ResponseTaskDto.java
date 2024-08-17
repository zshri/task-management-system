package org.example.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.example.taskmanagementsystem.model.TaskPriority;
import org.example.taskmanagementsystem.model.TaskStatus;

import java.time.Instant;

@Schema(description = "DTO для возврата задачи")
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
    private Instant createAt;
    private Instant updateAt;

}
