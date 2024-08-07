package org.example.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(description = "DTO для возврата комментария")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTaskCommentDto {

    private Long id;

    private String content;

    private Instant createAt;

    private Instant updateAt;

    private ResponseUserDto author;
}
