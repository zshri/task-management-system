package org.example.taskmanagementsystem.model;

import lombok.Getter;

@Getter
public enum TaskStatus {

    PENDING("в ожидании"),
    IN_PROGRESS("в процессе"),
    COMPLETED("завершено");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }
}
