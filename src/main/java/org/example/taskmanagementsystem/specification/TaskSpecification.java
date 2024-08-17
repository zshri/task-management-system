package org.example.taskmanagementsystem.specification;

import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.model.TaskFilterType;
import org.example.taskmanagementsystem.model.TaskPriority;
import org.example.taskmanagementsystem.model.TaskStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class TaskSpecification {

    public static Specification<Task> filterTasks(Long userId, TaskStatus status, TaskPriority priority, TaskFilterType filterType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            switch (filterType) {
                case AUTHOR -> predicates.add(criteriaBuilder.equal(root.get("author").get("id"), userId));
                case ASSIGNEE -> predicates.add(criteriaBuilder.equal(root.get("assignee").get("id"), userId));
                case ALL -> predicates.add(criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("author").get("id"), userId),
                        criteriaBuilder.equal(root.get("assignee").get("id"), userId)));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
