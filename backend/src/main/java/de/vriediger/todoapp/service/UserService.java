package de.vriediger.todoapp.service;

import de.vriediger.todoapp.model.User;
import de.vriediger.todoapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<String> getOtherUsernames() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("User nicht gefunden: " + currentUsername));

        return userRepository.findAll().stream()
                .map(User::getUsername)
                .filter(username -> !username.equals(currentUsername))
                .toList();
    }
}
