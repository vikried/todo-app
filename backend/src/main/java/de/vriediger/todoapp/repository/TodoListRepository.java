package de.vriediger.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.vriediger.todoapp.model.TodoList;
import de.vriediger.todoapp.model.User;

import java.util.List;

public interface TodoListRepository extends JpaRepository<TodoList, Long>{

    List<TodoList> findByTemplate(boolean isTemplate);

    @Query("SELECT DISTINCT t FROM TodoList t LEFT JOIN t.sharedWith s " +
            "WHERE t.template = :template AND (t.user = :user OR s = :user)")
    List<TodoList> findAccessibleLists(@Param("template") boolean template, @Param("user") User user);
}
