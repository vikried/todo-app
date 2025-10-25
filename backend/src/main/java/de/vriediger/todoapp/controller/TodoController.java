package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.dto.TodoDto;
import de.vriediger.todoapp.service.TodoService;
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

import java.net.URI;
import java.util.List;

@Tag(
        name = "Todo Controller",
        description = "Verwaltet Todos"
)
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TodoController {

    private final TodoService service;

    @Operation(summary = "Alle Todos abrufen",
               description = "Gibt eine Liste aller Todos zurück.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste erfolgreich abgerufen",
                content = @Content(
                        mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = TodoDto.class))
                ))
    })
    @GetMapping
    public ResponseEntity<List<TodoDto>> getAllTodos() {
        return ResponseEntity.ok(service.getAllTodos());
    }

    @Operation(
        summary = "Todo erstellen",
        description = "Erstellt ein neues Todo und gibt es zurück."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Todo erfolgreich erstellt",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TodoDto.class)
                )),
        @ApiResponse(responseCode = "400", description = "Ungültige Eingabe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TodoDto> createTodo(@RequestBody TodoDto todo) {
        TodoDto created = service.createTodo(todo);
        return ResponseEntity
            .created(URI.create("/api/todos/" + created.getId()))
            .body(created);
    }

    @Operation(
        summary = "Todo aktualisieren",
        description = "Aktualisiert die Eigenschaften eines Todos."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Todo erfolgreich aktualisiert",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = TodoDto.class))),
        @ApiResponse(responseCode = "404", description = "Todo nicht gefunden", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<TodoDto> updateTodo(@PathVariable Long id, @RequestBody TodoDto todo) {
        return ResponseEntity.ok(service.updateTodo(id, todo));
    }

    @Operation(
        summary = "Todo löschen",
        description = "Löscht ein Todo anhand seiner ID."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Todo erfolgreich gelöscht")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        service.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}