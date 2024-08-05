package org.example.taskmanagementsystem.model.dto;

import lombok.*;
import org.example.taskmanagementsystem.model.TaskPriority;
import org.example.taskmanagementsystem.model.TaskStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskDto {

    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long assignee;

}
