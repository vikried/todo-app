package de.vriediger.todoapp.service;

import de.vriediger.todoapp.dto.CategoryDto;
import de.vriediger.todoapp.mapper.CategoryMapper;
import de.vriediger.todoapp.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getCategoriesByList(Long taskListId) {
        return categoryRepository.findByTodoListId(taskListId)
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryDto createCategory(CategoryDto category) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toEntity(category)));
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}