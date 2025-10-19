package de.vriediger.todoapp.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class TodoListDto {
    private Long id;

    private String name;

    private boolean template = false;

    private List<TodoDto> todos = new ArrayList<>();
}