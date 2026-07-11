package de.vriediger.todoapp.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Erwartetes JSON-Format für den Template-Import, z. B.:
 * {
 *   "categories": [
 *     { "name": "Dokumente", "todos": ["Personalausweis", "Reisepass"] }
 *   ],
 *   "todos": ["Unkategorisiertes Todo"]
 * }
 */
@Data
public class TemplateImportDto {

    private List<CategoryImport> categories = new ArrayList<>();
    private List<String> todos = new ArrayList<>();

    @Data
    public static class CategoryImport {
        private String name;
        private List<String> todos = new ArrayList<>();
    }
}
