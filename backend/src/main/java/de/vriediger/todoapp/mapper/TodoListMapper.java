package de.vriediger.todoapp.mapper;

import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.model.TodoList;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TodoMapper.class, CategoryMapper.class}
)
public interface TodoListMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "template", target = "template")
    @Mapping(source = "todos", target = "todos")
    @Mapping(source = "categories", target = "categories")
    TodoListDto toDto(TodoList todoList);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "template", target = "template")
    @Mapping(source = "todos", target = "todos")
    @Mapping(source = "categories", target = "categories")
    TodoList toEntity(TodoListDto todoListDto);

}