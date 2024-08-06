package org.example.taskmanagementsystem.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskCommentDto {

    @NotBlank(message = "Content cannot be blank")
    private String content;
    @Positive(message = "Task ID must be a positive number")
    private Long taskId;
}
