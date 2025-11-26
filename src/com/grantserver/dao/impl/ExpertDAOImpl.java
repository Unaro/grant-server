package com.grantserver.dao.impl;

import com.grantserver.dao.ExpertDAO;
import com.grantserver.model.Expert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ExpertDAOImpl implements ExpertDAO {

    private final Map<Long, Expert> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Expert save(Expert expert) {
        if (expert.id == null) {
            expert.id = idGenerator.incrementAndGet();
        }
        storage.put(expert.id, expert);
        return expert;
    }

    @Override
    public Expert findById(Long id) {
        return storage.get(id);
    }

    @Override
    public Expert findByLogin(String login) {
        return storage.values().stream()
                .filter(e -> e.login.equals(login))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Expert> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean delete(Long id) {
        return storage.remove(id) != null;
    }
}