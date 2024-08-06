package org.example.taskmanagementsystem.model.dto;


import lombok.Data;

@Data
public class ResponseError {
    private int status;
    private String message;
    private String error;
    private String timestamp;
}