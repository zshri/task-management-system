package org.example.taskmanagementsystem.service;


import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanagementsystem.model.TaskStatus;
import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.RequestTaskDto;
import org.example.taskmanagementsystem.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

//    private final TaskRepository taskRepository;


//    public Page<RequestTaskDto> getTaskPage(TaskStatus taskStatus, User user, int page, int size, String sortBy, Double minPrice, Double maxPrice) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

//        Specification<Product> specification = (root, query, criteriaBuilder) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
////            if (filter != null && !filter.isEmpty()) {
////                predicates.add(criteriaBuilder.like(root.get("fieldName"), "%" + filter + "%"));
////            }
//            if (category != null) {
//                predicates.add(criteriaBuilder.equal(root.get("category"), category));
//            }
//            if (minPrice != null) {
//                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
//            }
//            if (maxPrice != null) {
//                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
//            }
//            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
//        };
//
//        return taskRepository.findAll(specification, pageable);
//    }


    public boolean save(RequestTaskDto requestTaskDto, User user) {
          return true;
    }


    public boolean update(RequestTaskDto requestTaskDto, User user) {
        return true;

    }

    public boolean delete(Long taskId, User user) {
        return true;

    }

    
}
