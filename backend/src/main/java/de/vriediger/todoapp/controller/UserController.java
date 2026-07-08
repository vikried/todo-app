package de.vriediger.todoapp.controller;

import de.vriediger.todoapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(
        name = "User Controller",
        description = "Verwaltet Nutzer"
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Andere Nutzer abrufen",
            description = "Gibt die Benutzernamen aller registrierten Nutzer außer dem aktuell eingeloggten zurück, z.B. für die Listen-Freigabe."
    )
    @GetMapping
    public ResponseEntity<List<String>> getOtherUsers() {
        return ResponseEntity.ok(userService.getOtherUsernames());
    }
}
