package com.grantserver.service.impl;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.ParticipantDAO;
import com.grantserver.dto.request.ParticipantRegisterDTO;
import com.grantserver.dto.response.ParticipantDTO;
import com.grantserver.model.Participant;
import com.grantserver.service.ParticipantService;

public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantDAO participantDAO;

    public ParticipantServiceImpl() {
        // Получаем зависимость через наш Registry (ручной Dependency Injection)
        this.participantDAO = ServiceRegistry.getInstance().get(ParticipantDAO.class);
    }

    @Override
    public ParticipantDTO register(ParticipantRegisterDTO dto) {
        // 1. Валидация: проверка уникальности логина
        if (participantDAO.findByLogin(dto.login) != null) {
            throw new RuntimeException("Login '" + dto.login + "' is already taken.");
        }

        // 2. Маппинг DTO -> Entity (без Mapper-библиотек делаем вручную)
        Participant participant = new Participant();
        participant.firmName = dto.firmName;
        participant.manager = dto.manager;
        participant.login = dto.login;
        participant.password = dto.password; // В реальном проекте тут нужен хеш (SHA-256)!

        // 3. Сохранение
        Participant saved = participantDAO.save(participant);

        // 4. Маппинг Entity -> ResponseDTO
        return new ParticipantDTO(saved);
    }
}