package org.example.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "DTO для создания и обновления комментария")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTaskCommentDto {

    @NotBlank(message = "Content cannot be blank")
    @Size(min = 1, max = 500, message = "Content must be between 1 and 500 characters")
    @Schema(description = "Содержимое комментария", example = "Это пример комментария", required = true)
    private String content;

    @NotNull(message = "Task ID cannot be null")
    @Positive(message = "Task ID must be a positive number")
    @Schema(description = "Идентификатор задачи, к которой относится комментарий", example = "1", required = true)
    private Long taskId;
}
