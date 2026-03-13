package com.grantserver.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.grantserver.common.db.Database;
import com.grantserver.dao.ParticipantDAO;
import com.grantserver.model.Participant;

public class ParticipantDAOImpl implements ParticipantDAO {
    
    private final Map<Long, Participant> table;
    private final Database db;

    public ParticipantDAOImpl() {
        this.db = Database.getInstance();
        this.table = db.getParticipantsTable();
    }

    @Override
    public Participant save(Participant participant) {
        if (participant.id == null) {
            participant.id = db.nextParticipantId();
        }
        table.put(participant.id, participant);
        return participant;
    }

    @Override
    public Participant findById(Long id) {
        return table.get(id);
    }

    @Override
    public Participant findByLogin(String login) {
        return table.values().stream()
                .filter(p -> p.login.equals(login))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Participant> findAll() {
        return new ArrayList<>(table.values());
    }

    @Override
    public boolean delete(Long id) {
        return table.remove(id) != null;
    }
}