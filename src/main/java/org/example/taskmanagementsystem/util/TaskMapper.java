package org.example.taskmanagementsystem.util;

import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.model.dto.ResponseTaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "priority", target = "priority")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "assignee", target = "assignee")
    @Mapping(source = "createAt", target = "createAt")
    @Mapping(source = "updateAt", target = "updateAt")
    ResponseTaskDto toDto(Task task);
}