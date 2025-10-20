package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.service.TodoListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TodoListController {

    private final TodoListService todoListService;

    @GetMapping
    public List<TodoListDto> getAllTodoLists() {
        return todoListService.getAllLists();
    }

    @GetMapping("/templates")
    public List<TodoListDto> getAllTemplates() {
        return todoListService.getAllTemplates();
    }

    @PostMapping
    public TodoListDto createTodoList(@RequestBody TodoListDto todoList) {
        return todoListService.createList(todoList);
    }

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<TodoListDto> createListFromTemplate(
            @PathVariable Long templateId, @RequestBody String newListName) {
        TodoListDto listFromTemplate = todoListService.createListFromTemplate(templateId, newListName);
        return ResponseEntity.ok(listFromTemplate);
    }

    @PutMapping("/{id}/categories")
    public ResponseEntity<TodoListDto> addCategoryToTodoList(
            @PathVariable Long id, @RequestBody Long categoryId) {
        TodoListDto listFromTemplate = todoListService.addCategoryToTodoList(id, categoryId);
        return ResponseEntity.ok(listFromTemplate);
    }
}
