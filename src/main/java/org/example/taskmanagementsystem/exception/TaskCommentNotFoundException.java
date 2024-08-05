package org.example.taskmanagementsystem.exception;

public class TaskCommentNotFoundException extends Exception {
    public TaskCommentNotFoundException(String taskNotFound) {
        super(taskNotFound);
    }
}
