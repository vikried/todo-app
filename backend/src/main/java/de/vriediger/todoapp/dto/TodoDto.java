package de.vriediger.todoapp.dto;

import de.vriediger.todoapp.model.TodoList;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TodoDto implements Comparable<TodoDto> {
    private Long id;

    private String title;
    private boolean done;

    private Long todoListId;

    private Long categoryId;

    @Override
    public int compareTo(TodoDto other) {
        return this.getId().compareTo(other.getId());
    }
}