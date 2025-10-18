package de.vriediger.todoapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.vriediger.todoapp.model.Task;
import de.vriediger.todoapp.model.TodoList;
import de.vriediger.todoapp.repository.TemplateRepository;
import de.vriediger.todoapp.repository.TodoListRepository;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TodoListController {
    private final TodoListRepository listRepo;
    private final TemplateRepository templateRepo;

    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<TodoList> createListFromTemplate(
            @PathVariable Long templateId,
            @RequestBody String newListName) {
        var template = templateRepo.findById(templateId).orElse(null);
        if (template == null)
            return ResponseEntity.notFound().build();

        TodoList list = new TodoList();
        list.setName(newListName);
        list.setTemplate(template);

        // Kopiere Standard-Tasks aus Template
        for (Task t : template.getDefaultTasks()) {
            Task copy = new Task();
            copy.setTitle(t.getTitle());
            copy.setDone(false);
            copy.setTodoList(list);
            list.getTasks().add(copy);
        }

        TodoList saved = listRepo.save(list);
        return ResponseEntity.ok(saved);
    }
}
