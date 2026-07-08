package de.vriediger.todoapp.mapper;

import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.model.TodoList;
import de.vriediger.todoapp.model.User;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    @Mapping(target = "ownerUsername", expression = "java(todoList.getUser() != null ? todoList.getUser().getUsername() : null)")
    @Mapping(target = "sharedWith", expression = "java(mapUsernames(todoList.getSharedWith()))")
    TodoListDto toDto(TodoList todoList);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "template", target = "template")
    @Mapping(source = "todos", target = "todos")
    @Mapping(source = "categories", target = "categories")
    @Mapping(target = "sharedWith", ignore = true)
    TodoList toEntity(TodoListDto todoListDto);

    default List<String> mapUsernames(Set<User> users) {
        if (users == null) {
            return new ArrayList<>();
        }
        return users.stream().map(User::getUsername).toList();
    }

}