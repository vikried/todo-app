package de.vriediger.todoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}