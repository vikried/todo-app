package de.vriediger.todoapp.service;

import de.vriediger.todoapp.dto.CategoryDto;
import de.vriediger.todoapp.mapper.CategoryMapper;
import de.vriediger.todoapp.model.Category;
import de.vriediger.todoapp.model.Todo;
import de.vriediger.todoapp.repository.CategoryRepository;
import de.vriediger.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TodoRepository todoRepository;

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public List<CategoryDto> getCategoriesByList(Long taskListId) {
        return categoryRepository.findByTodoListId(taskListId)
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public CategoryDto createCategory(CategoryDto category) {
        return categoryMapper.toDto(categoryRepository.save(categoryMapper.toEntity(category)));
    }

    public void addTodoToCategory(Long categoryId, Long todoId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Todo todo = todoRepository
                .findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        category.getTodos().add(todo);
        todo.setCategory(category);
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}