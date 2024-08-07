package org.example.taskmanagementsystem.exception;

public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String taskNotFound) {super(taskNotFound);}
}
