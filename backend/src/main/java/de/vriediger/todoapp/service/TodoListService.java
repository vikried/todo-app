package de.vriediger.todoapp.service;

import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.mapper.TodoListMapper;
import de.vriediger.todoapp.model.Category;
import de.vriediger.todoapp.model.Todo;
import de.vriediger.todoapp.model.TodoList;
import de.vriediger.todoapp.repository.CategoryRepository;
import de.vriediger.todoapp.repository.TodoListRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoListService {

    private final TodoListRepository todoListRepository;
    private final TodoListMapper todoListMapper;
    private final CategoryRepository categoryRepository;

    public List<TodoListDto> getAllLists() {
        return todoListRepository.findByTemplate(false)
                .stream()
                .map(todoListMapper::toDto)
                .toList();
    }

    public TodoListDto getListById(Long id) {
        return todoListMapper.toDto(todoListRepository.findById(id).orElseThrow(() -> new RuntimeException("List not found")));
    }

    public List<TodoListDto> getAllTemplates() {
        return todoListRepository.findByTemplate(true)
                .stream()
                .map(todoListMapper::toDto)
                .toList();
    }

    @Transactional
    public TodoListDto createListFromTemplate(Long templateId, String name) {
        TodoList template = todoListRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template nicht gefunden"));

        // Neue Liste erstellen
        TodoList newList = new TodoList();
        newList.setName(name);
        newList.setTemplate(false);

        // Kategorien klonen
        for (Category templateCategory : template.getCategories()) {
            Category newCategory = new Category();
            newCategory.setName(templateCategory.getName());
            newCategory.setTodoList(newList);

            // Todos klonen
            for (Todo templateTodo : templateCategory.getTodos()) {
                Todo newTodo = new Todo();
                newTodo.setTitle(templateTodo.getTitle());
                newTodo.setDone(false);
                newTodo.setCategory(newCategory);
                newTodo.setTodoList(newList);
                newCategory.getTodos().add(newTodo);
            }

            newList.getCategories().add(newCategory);
        }

        // Todos ohne Kategorie (falls vorhanden)
        for (Todo templateTodo : template.getTodos()) {
            if (templateTodo.getCategory() == null) {
                Todo newTodo = new Todo();
                newTodo.setTitle(templateTodo.getTitle());
                newTodo.setDone(false);
                newTodo.setTodoList(newList);
                newList.getTodos().add(newTodo);
            }
        }

        return todoListMapper.toDto(todoListRepository.save(newList));
    }

    public TodoListDto createList(TodoListDto todoListDto) {
        var todoList = todoListMapper.toEntity(todoListDto);
        return todoListMapper.toDto(todoListRepository.save(todoList));
    }

    public TodoListDto addCategoryToTodoList(Long todoListId, Long categoryId) {
        var todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new RuntimeException("TodoList not found"));
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.getTodos().forEach(todo -> todo.setTodoList(todoList));
        todoList.getCategories().add(category);
        category.setTodoList(todoList);
        return todoListMapper.toDto(todoListRepository.save(todoList));
    }

    public TodoListDto updateTodoList(Long id, TodoListDto todoListDto) {
        var todoList = todoListRepository.findById(id).orElseThrow(() -> new RuntimeException("TodoList not found"));
        todoList.setName(todoListDto.getName());
        todoList.setTemplate(todoListDto.getTemplate());
        todoListRepository.save(todoList);
        return todoListMapper.toDto(todoList);
    }

    public void deleteTodoList(Long todoListId) {
        todoListRepository.deleteById(todoListId);
    }
}