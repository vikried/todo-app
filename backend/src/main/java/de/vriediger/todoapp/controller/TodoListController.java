package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.service.TodoListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

@Tag(
        name = "TodoList Controller",
        description = "Verwaltet Todo-Listen"
)
@RestController
@RequestMapping("/api/lists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TodoListController {

    private final TodoListService todoListService;

    @Operation(summary = "Alle Todo-Listen abrufen",
            description = "Gibt alle Todo-Listen zurück.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste erfolgreich abgerufen",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TodoListDto.class))
                    ))
    })
    @GetMapping
    public ResponseEntity<List<TodoListDto>> getAllTodoLists() {
        return ResponseEntity.ok(todoListService.getAllLists());
    }

    @Operation(summary = "Alle Templates abrufen",
            description = "Gibt eine Liste der Templates zurück.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste erfolgreich abgerufen",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TodoListDto.class))
                    ))
    })
    @GetMapping("/templates")
    public ResponseEntity<List<TodoListDto>> getAllTemplates() {
        return ResponseEntity.ok(todoListService.getAllTemplates());
    }

    @Operation(
            summary = "Neue Todo-Liste erstellen",
            description = "Erstellt eine neue Todo-Liste und gibt sie zurück."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo-Liste erfolgreich erstellt",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoListDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TodoListDto> createTodoList(@RequestBody TodoListDto todoList) {
        var created = todoListService.createList(todoList);
        return ResponseEntity
                .created(URI.create("/api/lists/" + created.getId()))
                .body(created);
    }

    @Operation(
            summary = "Erstellt eine Todo-Liste aus einem Template",
            description = "Erstellt eine neue Todo-Liste aus einem Template und gibt sie zurück."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo-Liste erfolgreich erstellt",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoListDto.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe", content = @Content)
    })
    @PostMapping("/from-template/{templateId}")
    public ResponseEntity<TodoListDto> createListFromTemplate(
            @PathVariable Long templateId, @RequestBody String newListName) {
        TodoListDto listFromTemplate = todoListService.createListFromTemplate(templateId, newListName);
        return ResponseEntity.ok(listFromTemplate);
    }

    @Operation(
            summary = "Kategorie einer Todo-Liste hinzufügen",
            description = "Fügt eine bestehende Kategorie zu einer Todo-Liste hinzu."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kategorie erfolgreich hinzugefügt"),
            @ApiResponse(responseCode = "404", description = "Todo-Liste nicht gefunden", content = @Content)
    })
    @PutMapping("/{id}/categories")
    public ResponseEntity<TodoListDto> addCategoryToTodoList(
            @PathVariable Long id, @RequestBody Long categoryId) {
        TodoListDto listFromTemplate = todoListService.addCategoryToTodoList(id, categoryId);
        return ResponseEntity.ok(listFromTemplate);
    }

    @Operation(
            summary = "Todo-Liste aktualisieren",
            description = "Aktualisiert Eigenschaften einer Todo-Liste."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo-Liste erfolgreich aktualisiert",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TodoListDto.class))),
            @ApiResponse(responseCode = "404", description = "Todo-Liste nicht gefunden", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TodoListDto> updateTodoList(@PathVariable Long id, @RequestBody TodoListDto todoList) {
        return ResponseEntity.ok(todoListService.updateTodoList(id, todoList));
    }

    @Operation(
            summary = "Todo-Liste löschen",
            description = "Löscht eine Todo-Liste anhand ihrer ID."
        )
        @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Todo-Liste erfolgreich gelöscht")
        })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoList(@PathVariable Long id) {
        todoListService.deleteTodoList(id);
        return ResponseEntity.noContent().build();
    }
}
