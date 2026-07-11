package de.vriediger.todoapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.vriediger.todoapp.dto.TemplateImportDto;
import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.mapper.TodoListMapper;
import de.vriediger.todoapp.model.Category;
import de.vriediger.todoapp.model.Todo;
import de.vriediger.todoapp.model.TodoList;
import de.vriediger.todoapp.model.User;
import de.vriediger.todoapp.repository.CategoryRepository;
import de.vriediger.todoapp.repository.TodoListRepository;
import de.vriediger.todoapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TodoListService {

    private final TodoListRepository todoListRepository;
    private final TodoListMapper todoListMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User nicht gefunden: " + username));
    }

    private boolean isOwner(TodoList todoList, User user) {
        return todoList.getUser() != null && Objects.equals(todoList.getUser().getId(), user.getId());
    }

    private boolean hasAccess(TodoList todoList, User user) {
        if (todoList.isTemplate() || isOwner(todoList, user)) {
            return true;
        }
        return todoList.getSharedWith().stream().anyMatch(shared -> Objects.equals(shared.getId(), user.getId()));
    }

    private void requireAccess(TodoList todoList, User user) {
        if (!hasAccess(todoList, user)) {
            throw new AccessDeniedException("Kein Zugriff auf diese Todo-Liste");
        }
    }

    private void requireOwner(TodoList todoList, User user) {
        if (!todoList.isTemplate() && !isOwner(todoList, user)) {
            throw new AccessDeniedException("Nur der Eigentümer darf diese Aktion ausführen");
        }
    }

    public List<TodoListDto> getAllLists() {
        return todoListRepository.findAccessibleLists(false, getCurrentUser())
                .stream()
                .map(todoListMapper::toDto)
                .toList();
    }

    public TodoListDto getListById(Long id) {
        var todoList = todoListRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Liste nicht gefunden"));
        requireAccess(todoList, getCurrentUser());
        return todoListMapper.toDto(todoList);
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
        newList.setUser(getCurrentUser());

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

    @Transactional
    public TodoListDto importFromFile(MultipartFile file, String name, boolean template) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Datei ist leer");
        }

        TodoList newList = new TodoList();
        newList.setName(name);
        newList.setTemplate(template);
        newList.setUser(getCurrentUser());

        try {
            if (isJson(file)) {
                importJson(file, newList);
            } else {
                importCsv(file, newList);
            }
        } catch (IOException | RuntimeException e) {
            throw new IllegalArgumentException("Datei konnte nicht gelesen werden: " + e.getMessage());
        }

        if (newList.getCategories().isEmpty() && newList.getTodos().isEmpty()) {
            throw new IllegalArgumentException("Datei enthält keine importierbaren Einträge");
        }

        return todoListMapper.toDto(todoListRepository.save(newList));
    }

    private boolean isJson(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return (filename != null && filename.toLowerCase(Locale.ROOT).endsWith(".json"))
                || MediaType.APPLICATION_JSON_VALUE.equals(file.getContentType());
    }

    private void importJson(MultipartFile file, TodoList list) throws IOException {
        TemplateImportDto payload = objectMapper.readValue(file.getInputStream(), TemplateImportDto.class);
        for (TemplateImportDto.CategoryImport categoryImport : payload.getCategories()) {
            if (categoryImport.getName() == null || categoryImport.getName().isBlank()) {
                continue;
            }
            Category category = addCategory(list, categoryImport.getName());
            for (String title : categoryImport.getTodos()) {
                addTodo(list, category, title);
            }
        }
        for (String title : payload.getTodos()) {
            addTodo(list, null, title);
        }
    }

    private void importCsv(MultipartFile file, TodoList list) throws IOException {
        Map<String, Category> categoriesByName = new LinkedHashMap<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreSurroundingSpaces(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {
            for (CSVRecord record : parser) {
                if (!record.isMapped("title")) {
                    continue;
                }
                String title = record.get("title");
                if (title == null || title.isBlank()) {
                    continue;
                }
                String categoryName = record.isMapped("category") ? record.get("category") : null;
                Category category = null;
                if (categoryName != null && !categoryName.isBlank()) {
                    category = categoriesByName.computeIfAbsent(categoryName, n -> addCategory(list, n));
                }
                addTodo(list, category, title);
            }
        }
    }

    private Category addCategory(TodoList list, String name) {
        Category category = new Category();
        category.setName(name);
        category.setTodoList(list);
        list.getCategories().add(category);
        return category;
    }

    private void addTodo(TodoList list, Category category, String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDone(false);
        todo.setTodoList(list);
        todo.setCategory(category);
        if (category != null) {
            category.getTodos().add(todo);
        } else {
            list.getTodos().add(todo);
        }
    }

    public TodoListDto createList(TodoListDto todoListDto) {
        var todoList = todoListMapper.toEntity(todoListDto);
        todoList.setUser(getCurrentUser());
        return todoListMapper.toDto(todoListRepository.save(todoList));
    }

    public TodoListDto addCategoryToTodoList(Long todoListId, Long categoryId) {
        var todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("Liste nicht gefunden"));
        requireAccess(todoList, getCurrentUser());
        var category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Kategorie nicht gefunden"));
        category.getTodos().forEach(todo -> todo.setTodoList(todoList));
        todoList.getCategories().add(category);
        category.setTodoList(todoList);
        return todoListMapper.toDto(todoListRepository.save(todoList));
    }

    public TodoListDto updateTodoList(Long id, TodoListDto todoListDto) {
        var todoList = todoListRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Liste nicht gefunden"));
        requireAccess(todoList, getCurrentUser());
        todoList.setName(todoListDto.getName());
        todoList.setTemplate(todoListDto.getTemplate());
        todoListRepository.save(todoList);
        return todoListMapper.toDto(todoList);
    }

    public void deleteTodoList(Long todoListId) {
        var todoList = todoListRepository.findById(todoListId).orElseThrow(() -> new EntityNotFoundException("Liste nicht gefunden"));
        requireOwner(todoList, getCurrentUser());
        todoListRepository.deleteById(todoListId);
    }

    @Transactional
    public TodoListDto shareList(Long listId, String username) {
        var todoList = todoListRepository.findById(listId).orElseThrow(() -> new EntityNotFoundException("Liste nicht gefunden"));
        requireOwner(todoList, getCurrentUser());

        var targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Nutzer nicht gefunden: " + username));
        if (isOwner(todoList, targetUser)) {
            throw new IllegalArgumentException("Liste kann nicht mit dem Eigentümer geteilt werden");
        }

        todoList.getSharedWith().add(targetUser);
        return todoListMapper.toDto(todoListRepository.save(todoList));
    }

    @Transactional
    public TodoListDto unshareList(Long listId, String username) {
        var todoList = todoListRepository.findById(listId).orElseThrow(() -> new EntityNotFoundException("Liste nicht gefunden"));
        requireOwner(todoList, getCurrentUser());

        var targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Nutzer nicht gefunden: " + username));
        todoList.getSharedWith().removeIf(shared -> Objects.equals(shared.getId(), targetUser.getId()));
        return todoListMapper.toDto(todoListRepository.save(todoList));
    }
}