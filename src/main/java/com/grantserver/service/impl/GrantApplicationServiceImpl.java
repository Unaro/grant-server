package com.grantserver.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.dao.ParticipantDAO;
import com.grantserver.dto.request.GrantApplicationCreateDTO;
import com.grantserver.dto.response.GrantApplicationDTO;
import com.grantserver.model.GrantApplication;
import com.grantserver.model.GrantApplicationStatus;
import com.grantserver.model.Participant;
import com.grantserver.service.GrantApplicationService;

public class GrantApplicationServiceImpl implements GrantApplicationService {

    private final GrantApplicationDAO grantApplicationDAO;
    private final ParticipantDAO participantDAO;

    public GrantApplicationServiceImpl() {
        this.grantApplicationDAO = ServiceRegistry.getInstance().get(GrantApplicationDAO.class);
        this.participantDAO = ServiceRegistry.getInstance().get(ParticipantDAO.class);
    }

    @Override
    public GrantApplicationDTO create(GrantApplicationCreateDTO dto, Long ownerId) {
        GrantApplication app = new GrantApplication();
        app.title = dto.title;
        app.description = dto.description;
        app.fields = dto.fields;
        app.requestedSum = dto.requestedSum;
        app.status = GrantApplicationStatus.ACTIVE;

        Participant participant = participantDAO.findById(ownerId);
        if (participant == null) throw new IllegalArgumentException("Участник не найден");

        app.id = grantApplicationDAO.generateId(); 

        participant.addApplication(app);

        return new GrantApplicationDTO(app, ownerId);
    }

    @Override
    public List<GrantApplicationDTO> getAll() {
        return participantDAO.findAll().stream()
                .flatMap(participant -> participant.getApplications().stream()
                        .map(app -> new GrantApplicationDTO(app, participant.id)))
                .collect(Collectors.toList());
    }

    @Override
    public List<GrantApplicationDTO> getByOwner(Long ownerId) {
        return grantApplicationDAO.findAllByOwnerId(ownerId).stream()
                .map(app -> new GrantApplicationDTO(app, ownerId))
                .collect(Collectors.toList());
    }
}