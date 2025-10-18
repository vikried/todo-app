package de.vriediger.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.vriediger.todoapp.model.TodoList;

public interface TodoListRepository extends JpaRepository<TodoList, Long>{

}
