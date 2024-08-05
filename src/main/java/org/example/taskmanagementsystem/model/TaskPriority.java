package org.example.taskmanagementsystem.model;

import lombok.Getter;

@Getter
public enum TaskPriority {

    HIGH("высокий"),
    MEDIUM("средний"),
    LOW("низкий");

    private final String displayName;

    TaskPriority(String displayName) {
        this.displayName = displayName;
    }

}
