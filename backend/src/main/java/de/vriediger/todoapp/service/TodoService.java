package de.vriediger.todoapp.service;

import java.util.List;

import de.vriediger.todoapp.dto.TodoDto;
import de.vriediger.todoapp.mapper.TodoMapper;
import org.springframework.stereotype.Service;

import de.vriediger.todoapp.model.Todo;
import de.vriediger.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository repo;
    private final TodoMapper todoMapper;

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

    public TodoDto toggleDone(Long id) {
        Todo todo = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        todo.setDone(!todo.isDone());
        Todo updated = repo.save(todo);
        return todoMapper.toDTO(updated);
    }

    public void deleteTodo(Long id) {
        repo.deleteById(id);
    }

}
