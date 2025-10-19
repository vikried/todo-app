package de.vriediger.todoapp.service;

import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.mapper.TodoListMapper;
import de.vriediger.todoapp.model.Todo;
import de.vriediger.todoapp.model.TodoList;
import de.vriediger.todoapp.repository.TodoListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoListService {

    private final TodoListRepository todoListRepository;
    private final TodoListMapper todoListMapper;

    public List<TodoListDto> getAllLists() {
        return todoListRepository.findByTemplate(false)
        .stream()
        .map(todoListMapper::toDto)
        .toList();
    }

    public List<TodoListDto> getAllTemplates() {
        return todoListRepository.findByTemplate(true)
        .stream()
        .map(todoListMapper::toDto)
        .toList();
    }

    public TodoListDto createListFromTemplate(Long templateId, String newListName) {
        TodoList template = todoListRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        TodoList newList = TodoList.builder()
                .name(newListName)
                .template(false)
                .build();

        List<Todo> copiedTodos = template.getTodos().stream()
                .map(t -> Todo.builder()
                        .title(t.getTitle())
                        .done(false)
                        .todoList(newList)
                        .build())
                .toList();

        newList.setTodos(copiedTodos);

        return todoListMapper.toDto(todoListRepository.save(newList));
    }

    public TodoListDto createList(TodoListDto todoListDto) {
        TodoList todoList = todoListMapper.toEntity(todoListDto);
        return todoListMapper.toDto(todoListRepository.save(todoList));
    }
}