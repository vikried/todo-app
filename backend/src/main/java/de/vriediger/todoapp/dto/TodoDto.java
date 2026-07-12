package de.vriediger.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto implements Comparable<TodoDto> {
    private Long id;

    @NotBlank(message = "Titel darf nicht leer sein")
    private String title;
    private Boolean done;

    private Long todoListId;

    private Long categoryId;

    /**
     * Nur für Eingaben: Kategorie anhand des Namens statt der ID setzen (legt
     * sie bei Bedarf in der Ziel-Liste an). Leerstring entfernt die Kategorie.
     * Wird ignoriert, wenn categoryId gesetzt ist.
     */
    private String categoryName;

    @Override
    public int compareTo(TodoDto other) {
        return this.getId().compareTo(other.getId());
    }
}