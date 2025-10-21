package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.dto.CategoryDto;
import de.vriediger.todoapp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Category Controller",
        description = "Verwaltet Kategorien und deren zugeordnete Todos"
)
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Alle Kategorien abrufen",
               description = "Gibt eine Liste aller Kategorien zurück.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste erfolgreich abgerufen",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))
                ))
    })
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

     @Operation(
        summary = "Kategorien nach Todo-Liste abrufen",
        description = "Gibt alle Kategorien zurück, die einer bestimmten Todo-Liste zugeordnet sind."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Kategorien erfolgreich abgerufen",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))
                )),
        @ApiResponse(responseCode = "404", description = "Liste nicht gefunden", content = @Content)
    })
    @GetMapping("/list/{listId}")
    public ResponseEntity<List<CategoryDto>> getByList(@PathVariable Long listId) {
        return ResponseEntity.ok(categoryService.getCategoriesByList(listId));
    }

    @Operation(
        summary = "Neue Kategorie erstellen",
        description = "Erstellt eine neue Kategorie und gibt sie zurück."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Kategorie erfolgreich erstellt",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CategoryDto.class)
                )),
        @ApiResponse(responseCode = "400", description = "Ungültige Eingabe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    @Operation(
        summary = "Kategorie löschen",
        description = "Löscht eine Kategorie anhand ihrer ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Kategorie erfolgreich gelöscht")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Todo einer Kategorie hinzufügen",
        description = "Fügt ein bestehendes Todo-Item zu einer Kategorie hinzu."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Todo erfolgreich hinzugefügt"),
        @ApiResponse(responseCode = "404", description = "Kategorie oder Todo nicht gefunden", content = @Content)
    })
    @PutMapping("/{id}/todos")
    public ResponseEntity<Object> addTodoToCategory(@PathVariable Long id, @RequestBody Long todoId) {
        categoryService.addTodoToCategory(id, todoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Kategorie aktualisieren",
        description = "Aktualisiert den Namen oder andere Eigenschaften einer Kategorie."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Kategorie erfolgreich aktualisiert",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = CategoryDto.class))),
        @ApiResponse(responseCode = "404", description = "Kategorie nicht gefunden", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }
}