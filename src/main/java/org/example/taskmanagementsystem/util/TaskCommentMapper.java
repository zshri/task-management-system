package org.example.taskmanagementsystem.util;

import org.example.taskmanagementsystem.model.TaskComment;
import org.example.taskmanagementsystem.model.dto.ResponseTaskCommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface TaskCommentMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "createAt", target = "createAt")
    @Mapping(source = "updateAt", target = "updateAt")
    @Mapping(source = "author", target = "author")
    ResponseTaskCommentDto toDto(TaskComment taskComment);
}