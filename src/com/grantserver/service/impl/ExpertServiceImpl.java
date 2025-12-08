package com.grantserver.service.impl;

import com.grantserver.common.auth.SessionManager;
import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.ExpertDAO;
import com.grantserver.dto.request.AuthRequestDTO;
import com.grantserver.dto.request.ExpertRegisterDTO;
import com.grantserver.dto.response.AuthResponseDTO;
import com.grantserver.dto.response.ExpertDTO;
import com.grantserver.model.Expert;
import com.grantserver.service.ExpertService;

import java.util.UUID;

public class ExpertServiceImpl implements ExpertService {

    private final ExpertDAO expertDAO;

    public ExpertServiceImpl() {
        this.expertDAO = ServiceRegistry.getInstance().get(ExpertDAO.class);
    }

    @Override
    public ExpertDTO register(ExpertRegisterDTO dto) {
        // 1. Проверка уникальности логина
        if (expertDAO.findByLogin(dto.login) != null) {
            throw new RuntimeException("Login '" + dto.login + "' is already taken.");
        }

        // 2. Маппинг
        Expert expert = new Expert();
        expert.firstName = dto.firstName;
        expert.lastName = dto.lastName;
        expert.fields = dto.fields;
        expert.login = dto.login;
        expert.password = dto.password;

        // 3. Сохранение
        Expert saved = expertDAO.save(expert);

        return new ExpertDTO(saved);
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO dto) {
        Expert expert = expertDAO.findByLogin(dto.login);

        if (expert == null || !expert.password.equals(dto.password)) {
            throw new RuntimeException("Invalid login or password");
        }

        // Генерируем токен
        String token = UUID.randomUUID().toString();

        // Сохраняем сессию
        SessionManager.getInstance().createExpertSession(token, expert.id);

        return new AuthResponseDTO(token);
    }

    @Override
    public ExpertDTO getById(Long id) {
        Expert expert = expertDAO.findById(id);
        if (expert == null) return null;
        return new ExpertDTO(expert);
    }
}