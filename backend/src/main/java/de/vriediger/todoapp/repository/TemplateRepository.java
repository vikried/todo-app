package de.vriediger.todoapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.vriediger.todoapp.model.ListTemplate;

public interface TemplateRepository extends JpaRepository<ListTemplate, Long> {
    
}
