package de.vriediger.todoapp.service;

import de.vriediger.todoapp.dto.LoginRequest;
import de.vriediger.todoapp.dto.LoginResponse;
import de.vriediger.todoapp.dto.RegisterRequest;
import de.vriediger.todoapp.model.User;
import de.vriediger.todoapp.repository.UserRepository;
import de.vriediger.todoapp.security.JwtUtil;
import de.vriediger.todoapp.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Ungültige Zugangsdaten"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Ungültige Zugangsdaten");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponse(token);
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Benutzername bereits vergeben");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(user);
    }

    public void logout(String token) {
        tokenBlacklistService.blacklist(token);
    }
}
