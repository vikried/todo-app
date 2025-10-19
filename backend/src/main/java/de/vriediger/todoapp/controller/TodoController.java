package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.dto.TodoDto;
import de.vriediger.todoapp.service.TodoService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService service;

    @GetMapping
    public List<TodoDto> getAllTodos() {
        return service.getAllTodos();
    }

    @PostMapping
    public TodoDto createTodo(@RequestBody TodoDto todo) {
        return service.createTodo(todo);
    }

    @PutMapping("/{id}/toggle")
    public TodoDto toggleDone(@PathVariable Long id) {
        return service.toggleDone(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        service.deleteTodo(id);
    }
}