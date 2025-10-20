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

    @PatchMapping("/{id}")
    public TodoDto updateTodo(@PathVariable Long id, @RequestBody TodoDto todo) {
        return service.updateTodo(id, todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        service.deleteTodo(id);
    }
}