package de.vriediger.todoapp.dto;

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

    private String title;
    private Boolean done;

    private Long todoListId;

    private Long categoryId;

    @Override
    public int compareTo(TodoDto other) {
        return this.getId().compareTo(other.getId());
    }
}