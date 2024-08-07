package org.example.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import org.example.taskmanagementsystem.model.TaskPriority;
import org.example.taskmanagementsystem.model.TaskStatus;


@Schema(description = "DTO для создания и обновления задачи")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskDto {

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = 50, message = "Content must be between 1 and 50 characters")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 1, max = 1000, message = "Content must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Status cannot be null")
    private TaskStatus status;

    @NotNull(message = "Priority cannot be null")
    private TaskPriority priority;

    @Positive(message = "Assignee ID must be a positive number")
    private Long assignee;

}
