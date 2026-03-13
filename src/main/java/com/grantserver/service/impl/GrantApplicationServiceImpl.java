package com.grantserver.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.dao.ParticipantDAO;
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
    public GrantApplication create(GrantApplication app, Long ownerId) {
        if (app.requestedSum <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше нуля");
        }

        Participant owner = participantDAO.findById(ownerId);
        if (owner == null) throw new IllegalArgumentException("Участник не найден");

        app.status = GrantApplicationStatus.ACTIVE;
        app.id = grantApplicationDAO.generateId();

        owner.addApplication(app);

        return app;
    }

    @Override
    public List<GrantApplication> getAll() {
        List<GrantApplication> allApps = new ArrayList<>();
        for (Participant p : participantDAO.findAll()) {
            allApps.addAll(p.getApplications());
        }
        return allApps;
    }

    @Override
    public List<GrantApplication> getByOwner(Long ownerId) {
        Participant p = participantDAO.findById(ownerId);
        if (p == null) return new ArrayList<>();
        return p.getApplications();
    }
}