package com.grantserver.service.impl;

import com.grantserver.common.auth.SessionManager;
import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.ExpertDAO;
import com.grantserver.model.Expert;
import com.grantserver.service.ExpertService;

public class ExpertServiceImpl implements ExpertService {

    private final ExpertDAO expertDAO;

    public ExpertServiceImpl() {
        this.expertDAO = ServiceRegistry.getInstance().get(ExpertDAO.class);
    }

    @Override
    public Expert register(Expert expert) {
        if (expertDAO.findByLogin(expert.login) != null) {
            throw new RuntimeException("Login '" + expert.login + "' is already taken.");
        }

        Expert saved = expertDAO.save(expert);

        return saved;
    }

    @Override
    public String login(String login, String password) {
        Expert expert = expertDAO.findByLogin(login);

        if (expert == null || !expert.password.equals(password)) {
            throw new RuntimeException("Invalid login or password");
        }

        String token = SessionManager.getInstance().createSession(expert.id);

        return token;
    }

    @Override
    public Expert getById(Long id) {
        Expert expert = expertDAO.findById(id);
        if (expert == null) return null;
        return expert;
    }
}