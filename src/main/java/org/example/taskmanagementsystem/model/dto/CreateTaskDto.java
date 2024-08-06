package org.example.taskmanagementsystem.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.example.taskmanagementsystem.model.TaskPriority;
import org.example.taskmanagementsystem.model.TaskStatus;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskDto {

    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @NotNull(message = "Status cannot be null")
    private TaskStatus status;
    @NotNull(message = "Priority cannot be null")
    private TaskPriority priority;
    @Positive(message = "Assignee ID must be a positive number")
    private Long assignee;

}
