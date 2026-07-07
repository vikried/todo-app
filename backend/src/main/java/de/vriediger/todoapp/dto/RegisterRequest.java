package de.vriediger.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Benutzername darf nicht leer sein") String username,
        @NotBlank(message = "Passwort darf nicht leer sein")
        @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein") String password) {}
