package com.grantserver.dao.impl;

import com.grantserver.common.db.Database;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.model.GrantApplication;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GrantApplicationDAOImpl implements GrantApplicationDAO {

    private final Map<Long, GrantApplication> table;
    private final Database db;

    public GrantApplicationDAOImpl() {
        this.db = Database.getInstance();
        this.table = db.getApplicationsTable();
    }

    @Override
    public GrantApplication save(GrantApplication application) {
        if (application.id == null) {
            application.id = db.nextApplicationId();
        }
        table.put(application.id, application);
        return application;
    }

    @Override
    public GrantApplication findById(Long id) {
        return table.get(id);
    }

    @Override
    public List<GrantApplication> findAll() {
        return new ArrayList<>(table.values());
    }

    @Override
    public List<GrantApplication> findAllByOwnerId(Long ownerId) {
        return table.values().stream()
                .filter(app -> app.ownerId != null && app.ownerId.equals(ownerId))
                .collect(Collectors.toList());
    }
}