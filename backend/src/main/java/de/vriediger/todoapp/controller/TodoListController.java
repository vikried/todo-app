package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.dto.CategoryDto;
import de.vriediger.todoapp.dto.ShareRequest;
import de.vriediger.todoapp.dto.TodoListDto;
import de.vriediger.todoapp.service.TodoListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(
            summary = "Todo-Liste anhand der ID finden",
            description = "Gibt eine Liste anhand ihrer Id zurück."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo-Liste gefunden",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TodoListDto.class))),
            @ApiResponse(responseCode = "404", description = "Todo-Liste nicht gefunden", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoListDto> findTodoList(@PathVariable Long id) {
        return ResponseEntity.ok(todoListService.getListById(id));
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
    public ResponseEntity<TodoListDto> createTodoList(@Valid @RequestBody TodoListDto todoList) {
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
            @PathVariable Long templateId, @RequestParam("newListName") String newListName) {
        TodoListDto listFromTemplate = todoListService.createListFromTemplate(templateId, newListName);
        return ResponseEntity.ok(listFromTemplate);
    }

    @Operation(
            summary = "Todo-Liste aus CSV oder JSON importieren",
            description = "Erstellt eine neue Todo-Liste (standardmäßig als Template) aus einer hochgeladenen "
                    + "CSV- oder JSON-Datei. CSV erwartet die Spalten \"category\" (optional) und \"title\"; "
                    + "JSON erwartet {\"categories\":[{\"name\":\"...\",\"todos\":[\"...\"]}],\"todos\":[\"...\"]}. "
                    + "Das Dateiformat wird an der Dateiendung bzw. am Content-Type erkannt."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todo-Liste erfolgreich importiert",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TodoListDto.class))),
            @ApiResponse(responseCode = "400", description = "Datei ungültig oder leer", content = @Content)
    })
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TodoListDto> importTodoList(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "template", defaultValue = "true") boolean template) {
        TodoListDto imported = todoListService.importFromFile(file, name, template);
        return ResponseEntity
                .created(URI.create("/api/lists/" + imported.getId()))
                .body(imported);
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
            @PathVariable Long id, @RequestBody CategoryDto category) {
        TodoListDto listFromTemplate = todoListService.addCategoryToTodoList(id, category.getId());
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
    public ResponseEntity<TodoListDto> updateTodoList(@PathVariable Long id, @Valid @RequestBody TodoListDto todoList) {
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

    @Operation(
            summary = "Todo-Liste mit einem Nutzer teilen",
            description = "Gibt einer Todo-Liste Zugriff für einen weiteren Nutzer. Nur der Eigentümer darf teilen."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste erfolgreich geteilt",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TodoListDto.class))),
            @ApiResponse(responseCode = "403", description = "Kein Eigentümer der Liste", content = @Content),
            @ApiResponse(responseCode = "404", description = "Liste oder Nutzer nicht gefunden", content = @Content)
    })
    @PostMapping("/{id}/share")
    public ResponseEntity<TodoListDto> shareTodoList(@PathVariable Long id, @Valid @RequestBody ShareRequest request) {
        return ResponseEntity.ok(todoListService.shareList(id, request.username()));
    }

    @Operation(
            summary = "Freigabe einer Todo-Liste entfernen",
            description = "Entzieht einem Nutzer den Zugriff auf eine Todo-Liste. Nur der Eigentümer darf dies tun."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Freigabe erfolgreich entfernt",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TodoListDto.class))),
            @ApiResponse(responseCode = "403", description = "Kein Eigentümer der Liste", content = @Content),
            @ApiResponse(responseCode = "404", description = "Liste oder Nutzer nicht gefunden", content = @Content)
    })
    @DeleteMapping("/{id}/share/{username}")
    public ResponseEntity<TodoListDto> unshareTodoList(@PathVariable Long id, @PathVariable String username) {
        return ResponseEntity.ok(todoListService.unshareList(id, username));
    }
}
