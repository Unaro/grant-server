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

public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantDAO participantDAO;

    public ParticipantServiceImpl() {
        this.participantDAO = ServiceRegistry.getInstance().get(ParticipantDAO.class);
    }

    @Override
    public ParticipantDTO register(ParticipantRegisterDTO dto) {
        if (participantDAO.findByLogin(dto.login) != null) {
            throw new RuntimeException("Login '" + dto.login + "' is already taken.");
        }

        Participant participant = new Participant();
        participant.firmName = dto.firmName;
        participant.manager = dto.manager;
        participant.login = dto.login;
        participant.password = dto.password; 

        Participant saved = participantDAO.save(participant);

        return new ParticipantDTO(saved);
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO dto) {
        Participant participant = participantDAO.findByLogin(dto.login);
        
        if (participant == null || !participant.password.equals(dto.password)) {
            throw new RuntimeException("Invalid login or password");
        }

        String token =SessionManager.getInstance().createSession(participant.id);

        return new AuthResponseDTO(token);
    }
}