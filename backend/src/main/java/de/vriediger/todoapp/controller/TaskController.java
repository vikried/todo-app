package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.model.Task;
import de.vriediger.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // wichtig fürs Frontend
public class TaskController {

    private final TaskRepository repo;

    @GetMapping
    public List<Task> getAllTasks() {
        return repo.findAll();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        return repo.save(task);
    }

    @PutMapping("/{id}/toggle")
    public Task updateTask(@PathVariable Long id) {
        return repo.findById(id)
                .map(task -> {
                    task.setDone(!task.isDone());
                    return repo.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        repo.deleteById(id);
    }
}