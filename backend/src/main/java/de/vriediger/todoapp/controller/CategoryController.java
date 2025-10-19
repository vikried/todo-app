package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.dto.CategoryDto;
import de.vriediger.todoapp.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/list/{listId}")
    public List<CategoryDto> getByList(@PathVariable Long listId) {
        return categoryService.getCategoriesByList(listId);
    }

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto dto) {
        return categoryService.createCategory(dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}