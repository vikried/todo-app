package de.vriediger.todoapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Boolean done;

    @ManyToOne
    @JoinColumn(name = "todo_list_id")
    private TodoList todoList;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}