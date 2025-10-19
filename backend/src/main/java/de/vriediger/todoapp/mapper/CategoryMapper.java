package de.vriediger.todoapp.mapper;

import de.vriediger.todoapp.dto.CategoryDto;
import de.vriediger.todoapp.model.Category;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TodoMapper.class)
public interface CategoryMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "todos", target = "todos")
    CategoryDto toDto(Category entity);

    @InheritInverseConfiguration
    Category toEntity(CategoryDto dto);
}