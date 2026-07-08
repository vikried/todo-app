package de.vriediger.todoapp.dto;

import jakarta.validation.constraints.NotBlank;

public record ShareRequest(@NotBlank(message = "Benutzername darf nicht leer sein") String username) {}
