package com.grantserver.common.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    
    private static final SessionManager INSTANCE = new SessionManager();
    
    // Хранилище: Токен -> ID пользователя
    private final Map<String, Long> participantSessions = new ConcurrentHashMap<>();
    private final Map<String, Long> expertSessions = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    // --- Участники ---
    public void createParticipantSession(String token, Long userId) {
        participantSessions.put(token, userId);
    }

    public Long getParticipantId(String token) {
        return participantSessions.get(token);
    }

    // --- Эксперты ---
    public void createExpertSession(String token, Long expertId) {
        expertSessions.put(token, expertId);
    }

    public Long getExpertId(String token) {
        return expertSessions.get(token);
    }
}