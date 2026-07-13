package de.vriediger.todoapp.service;

import de.vriediger.todoapp.dto.CategoryDto;
import de.vriediger.todoapp.mapper.CategoryMapper;
import de.vriediger.todoapp.repository.CategoryRepository;
import de.vriediger.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

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
        var category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        var todo = todoRepository
                .findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        category.getTodos().add(todo);
        todo.setCategory(category);
        todo.setTodoList(category.getTodoList());
        categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (!isNull(categoryDto.getName())) {
            category.setName(categoryDto.getName());
        }

        var updated = categoryRepository.save(category);
        return categoryMapper.toDto(updated);
    }
}