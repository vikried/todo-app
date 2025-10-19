package de.vriediger.todoapp.mapper;

import de.vriediger.todoapp.dto.TodoDto;
import de.vriediger.todoapp.model.Todo;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TodoMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "done", target = "done")
    @Mapping(source = "todoList.id", target = "todoListId")
    @Mapping(source ="category.id", target = "categoryId")
    TodoDto toDTO(Todo todo);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "done", target = "done")
    @Mapping(target = "todoList", ignore = true)
    @Mapping(target = "category", ignore = true)
    Todo toEntity(TodoDto todoDto);
}