package com.grantserver.service.impl;

import com.grantserver.common.auth.SessionManager;
import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.ParticipantDAO;
import com.grantserver.model.Participant;
import com.grantserver.service.ParticipantService;

public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantDAO participantDAO;

    public ParticipantServiceImpl() {
        this.participantDAO = ServiceRegistry.getInstance().get(ParticipantDAO.class);
    }

    @Override
    public Participant register(Participant participant) {
        if (participantDAO.findByLogin(participant.login) != null) {
            throw new RuntimeException("Login '" + participant.login + "' is already taken.");
        }

        Participant saved = participantDAO.save(participant);

        return saved;
    }

    @Override
    public String login(String login, String password) {
        Participant participant = participantDAO.findByLogin(login);
        
        if (participant == null || !participant.password.equals(password)) {
            throw new RuntimeException("Invalid login or password");
        }

        String token =SessionManager.getInstance().createSession(participant.id);

        return token;
    }
}