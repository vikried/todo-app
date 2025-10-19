package de.vriediger.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.vriediger.todoapp.model.TodoList;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Long>{

    List<TodoList> findByTemplate(boolean isTemplate);
}
