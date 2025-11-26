package com.grantserver.dao.impl;

import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.model.GrantApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class GrantApplicationDAOImpl implements GrantApplicationDAO {

    private final Map<Long, GrantApplication> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public GrantApplication save(GrantApplication application) {
        if (application.id == null) {
            application.id = idGenerator.incrementAndGet();
        }
        storage.put(application.id, application);
        return application;
    }

    @Override
    public GrantApplication findById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<GrantApplication> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<GrantApplication> findAllByOwnerId(Long ownerId) {
        return storage.values().stream()
                .filter(app -> app.ownerId != null && app.ownerId.equals(ownerId))
                .collect(Collectors.toList());
    }
}