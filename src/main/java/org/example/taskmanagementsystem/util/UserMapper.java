package org.example.taskmanagementsystem.util;


import org.example.taskmanagementsystem.model.User;
import org.example.taskmanagementsystem.model.dto.ResponseUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "name")
    ResponseUserDto toDto(User user);
}