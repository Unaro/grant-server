package com.grantserver.dao;

import com.grantserver.model.Participant;
import java.util.List;

public interface ParticipantDAO {
    Participant save(Participant participant);
    Participant findById(Long id);
    Participant findByLogin(String login);
    List<Participant> findAll();
    boolean delete(Long id);
}