package org.example.taskmanagementsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
