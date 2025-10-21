package de.vriediger.todoapp.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {

    private Long id;

    private String name;

    @Builder.Default
    private List<TodoDto> todos = new ArrayList<>();
}