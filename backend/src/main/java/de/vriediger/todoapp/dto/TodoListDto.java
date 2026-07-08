package de.vriediger.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @NotBlank(message = "Name darf nicht leer sein")
    private String name;

    @JsonProperty("template")
    @Builder.Default
    private Boolean template = false;

    @JsonProperty("todos")
    @Builder.Default
    private List<TodoDto> todos = new ArrayList<>();

    @JsonProperty("categories")
    @Builder.Default
    private List<CategoryDto> categories = new ArrayList<>();

    @JsonProperty("ownerUsername")
    private String ownerUsername;

    @JsonProperty("sharedWith")
    @Builder.Default
    private List<String> sharedWith = new ArrayList<>();
}