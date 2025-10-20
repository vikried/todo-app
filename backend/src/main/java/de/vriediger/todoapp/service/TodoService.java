package de.vriediger.todoapp.service;

import java.util.List;

import de.vriediger.todoapp.dto.TodoDto;
import de.vriediger.todoapp.mapper.TodoMapper;
import de.vriediger.todoapp.model.Category;
import de.vriediger.todoapp.model.TodoList;
import de.vriediger.todoapp.repository.CategoryRepository;
import de.vriediger.todoapp.repository.TodoListRepository;
import org.springframework.stereotype.Service;

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

    public TodoDto createTodo(TodoDto todoDto) {
        Todo todo = todoMapper.toEntity(todoDto);
        Todo saved = repo.save(todo);
        return todoMapper.toDTO(saved);
    }

    public TodoDto updateTodo(Long id, TodoDto todoDto) {
        Todo todo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        todo.setDone(todoDto.isDone());
        if (!isNull(todoDto.getCategoryId())) {
            Category category = categoryRepository.findById(todoDto.getCategoryId()).orElse(null);
            todo.setCategory(category);
        }
        if (!isNull(todoDto.getTodoListId())) {
            TodoList todoList = todoListRepository.findById(todoDto.getTodoListId()).orElse(null);
            todo.setTodoList(todoList);
        }
        if (!isNull(todoDto.getTitle())) {
            todo.setTitle(todoDto.getTitle());
        }


        Todo updated = repo.save(todo);
        return todoMapper.toDTO(updated);
    }

    public void deleteTodo(Long id) {
        repo.deleteById(id);
    }

}
