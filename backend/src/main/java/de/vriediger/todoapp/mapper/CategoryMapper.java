package de.vriediger.todoapp.mapper;

import de.vriediger.todoapp.dto.CategoryDto;
import de.vriediger.todoapp.model.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = TodoMapper.class)
public interface CategoryMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "todos", target = "todos")
    CategoryDto toDto(Category entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "todos", target = "todos")
    Category toEntity(CategoryDto dto);
}