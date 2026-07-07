package de.vriediger.todoapp.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hält widerrufene JWTs bis zu ihrem Ablauf vor (In-Memory).
 * Bei mehreren Backend-Instanzen müsste dies durch einen geteilten Store (z. B. Redis) ersetzt werden.
 */
@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
