package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.service.TodoListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TodoListController {

    private final TodoListService todoListService;

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<TodoListDto> createListFromTemplate(
            @PathVariable Long templateId,
            @RequestBody String newListName) {
        TodoListDto listFromTemplate = todoListService.createListFromTemplate(templateId, newListName);
        return ResponseEntity.ok(listFromTemplate);
    }
}
