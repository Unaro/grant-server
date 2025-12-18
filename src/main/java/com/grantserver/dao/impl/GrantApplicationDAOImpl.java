package com.grantserver.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.grantserver.common.db.Database;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.model.GrantApplication;
import com.grantserver.model.Participant;


public class GrantApplicationDAOImpl implements GrantApplicationDAO {

    private final Map<Long, Participant> table;
    private final Database db;

    public GrantApplicationDAOImpl() {
        this.db = Database.getInstance();
        this.table = db.getParticipantsTable();
    }

    @Override
    public Long generateId() {
        return db.nextApplicationId();
    }

    @Override
    public GrantApplication findById(Long id) {
        return table.values().stream()
                .flatMap(participant -> participant.getApplications().stream())
                .filter(app -> app.id.equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<GrantApplication> findAll() {
        return new ArrayList<>(table.values()).stream()
                .flatMap(participant -> participant.getApplications().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<GrantApplication> findAllByOwnerId(Long ownerId) {
        return table.values().stream()
                .filter(owner -> owner.id.equals(ownerId))
                .flatMap(participant -> participant.getApplications().stream())
                .collect(Collectors.toList());
    }
}