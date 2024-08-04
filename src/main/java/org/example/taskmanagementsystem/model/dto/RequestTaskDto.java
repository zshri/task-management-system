package org.example.taskmanagementsystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class RequestTaskDto {
    private Long id;
    private String title;
    private String description;
    private String status;
}
