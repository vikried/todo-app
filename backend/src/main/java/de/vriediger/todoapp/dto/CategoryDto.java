package de.vriediger.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Name darf nicht leer sein")
    private String name;

    @Builder.Default
    private List<TodoDto> todos = new ArrayList<>();
}