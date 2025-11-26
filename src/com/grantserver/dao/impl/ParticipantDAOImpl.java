package com.grantserver.dao.impl;

import com.grantserver.dao.ParticipantDAO;
import com.grantserver.model.Participant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ParticipantDAOImpl implements ParticipantDAO {
    
    // Имитация таблицы в памяти
    private final Map<Long, Participant> storage = new ConcurrentHashMap<>();
    // Генератор ID (аналог SEQUENCE или AUTO_INCREMENT)
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Participant save(Participant participant) {
        if (participant.id == null) {
            // Новый участник - генерируем ID
            participant.id = idGenerator.incrementAndGet();
        }
        storage.put(participant.id, participant);
        return participant;
    }

    @Override
    public Participant findById(Long id) {
        return storage.get(id);
    }

    @Override
    public Participant findByLogin(String login) {
        // В реальной БД это было бы "SELECT * FROM participants WHERE login = ?"
        // Здесь перебираем мапу (для in-memory это быстро)
        return storage.values().stream()
                .filter(p -> p.login.equals(login))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Participant> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean delete(Long id) {
        return storage.remove(id) != null;
    }
}