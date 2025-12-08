package com.grantserver.service.impl;

import com.grantserver.common.auth.SessionManager;
import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.ParticipantDAO;
import com.grantserver.dto.request.AuthRequestDTO;
import com.grantserver.dto.request.ParticipantRegisterDTO;
import com.grantserver.dto.response.AuthResponseDTO;
import com.grantserver.dto.response.ParticipantDTO;
import com.grantserver.model.Participant;
import com.grantserver.service.ParticipantService;

import java.util.UUID;

public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantDAO participantDAO;

    public ParticipantServiceImpl() {
        // Получаем зависимость через Registry
        this.participantDAO = ServiceRegistry.getInstance().get(ParticipantDAO.class);
    }

    @Override
    public ParticipantDTO register(ParticipantRegisterDTO dto) {
        // 1. Валидация: проверка уникальности логина
        if (participantDAO.findByLogin(dto.login) != null) {
            throw new RuntimeException("Login '" + dto.login + "' is already taken.");
        }

        // 2. Маппинг DTO -> Entity
        Participant participant = new Participant();
        participant.firmName = dto.firmName;
        participant.manager = dto.manager;
        participant.login = dto.login;
        participant.password = dto.password; 

        // 3. Сохранение
        Participant saved = participantDAO.save(participant);

        // 4. Маппинг Entity -> ResponseDTO
        return new ParticipantDTO(saved);
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO dto) {
        // 1. Ищем пользователя
        Participant participant = participantDAO.findByLogin(dto.login);
        
        // 2. Проверяем существование и пароль
        if (participant == null || !participant.password.equals(dto.password)) {
            throw new RuntimeException("Invalid login or password");
        }

        // 3. Генерируем токен (UUID)
        String token = UUID.randomUUID().toString();
        
        // 4. Сохраняем сессию
        SessionManager.getInstance().createParticipantSession(token, participant.id);

        return new AuthResponseDTO(token);
    }
}