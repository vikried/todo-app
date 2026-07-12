package de.vriediger.todoapp.service;

import java.util.List;

import de.vriediger.todoapp.dto.TodoDto;
import de.vriediger.todoapp.mapper.TodoMapper;
import de.vriediger.todoapp.model.Category;
import de.vriediger.todoapp.model.TodoList;
import de.vriediger.todoapp.repository.CategoryRepository;
import de.vriediger.todoapp.repository.TodoListRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.vriediger.todoapp.model.Todo;
import de.vriediger.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository repo;
    private final TodoMapper todoMapper;
    private final CategoryRepository categoryRepository;
    private final TodoListRepository todoListRepository;

    public List<TodoDto> getAllTodos() {
        return repo.findAll().stream()
                .map(todoMapper::toDTO)
                .toList()
                .stream()
                .sorted()
                .toList();
    }

    public TodoDto findTodoById(Long id) {
        return repo.findById(id)
                .map(todoMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    @Transactional
    public TodoDto createTodo(TodoDto todoDto) {
        var todo = new Todo();
        todo.setTitle(todoDto.getTitle());
        todo.setDone(Boolean.TRUE.equals(todoDto.getDone()));

        if (!isNull(todoDto.getTodoListId())) {
            var todoList = todoListRepository.findById(todoDto.getTodoListId())
                    .orElseThrow(() -> new EntityNotFoundException("Liste nicht gefunden"));
            todo.setTodoList(todoList);

            if (!isNull(todoDto.getCategoryId())) {
                var category = categoryRepository.findById(todoDto.getCategoryId())
                        .orElseThrow(() -> new EntityNotFoundException("Kategorie nicht gefunden"));
                todo.setCategory(category);
            } else if (!isNull(todoDto.getCategoryName()) && !todoDto.getCategoryName().isBlank()) {
                todo.setCategory(findOrCreateCategory(todoList, todoDto.getCategoryName()));
            }
        }

        var saved = repo.save(todo);
        return todoMapper.toDTO(saved);
    }

    @Transactional
    public TodoDto updateTodo(Long id, TodoDto todoDto) {
        var todo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        todo.setDone(todoDto.getDone());

        TodoList targetList = todo.getTodoList();
        if (!isNull(todoDto.getTodoListId())) {
            targetList = todoListRepository.findById(todoDto.getTodoListId()).orElse(null);
            todo.setTodoList(targetList);
        }

        if (!isNull(todoDto.getCategoryId())) {
            var category = categoryRepository.findById(todoDto.getCategoryId()).orElse(null);
            todo.setCategory(category);
        } else if (!isNull(todoDto.getCategoryName())) {
            if (todoDto.getCategoryName().isBlank()) {
                todo.setCategory(null);
            } else if (targetList != null) {
                todo.setCategory(findOrCreateCategory(targetList, todoDto.getCategoryName()));
            }
        }

        if (!isNull(todoDto.getTitle())) {
            todo.setTitle(todoDto.getTitle());
        }

        var updated = repo.save(todo);
        return todoMapper.toDTO(updated);
    }

    public void deleteTodo(Long id) {
        repo.deleteById(id);
    }

    private Category findOrCreateCategory(TodoList todoList, String name) {
        String trimmedName = name.trim();
        return todoList.getCategories().stream()
                .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(trimmedName))
                .findFirst()
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setName(trimmedName);
                    category.setTodoList(todoList);
                    return categoryRepository.save(category);
                });
    }

}
