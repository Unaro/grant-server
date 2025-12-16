package com.grantserver.dao.impl;

import com.grantserver.common.db.Database;
import com.grantserver.dao.ExpertDAO;
import com.grantserver.model.Expert;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpertDAOImpl implements ExpertDAO {

    private final Map<Long, Expert> table;
    private final Database db;

    public ExpertDAOImpl() {
        this.db = Database.getInstance();
        this.table = db.getExpertsTable();
    }

    @Override
    public Expert save(Expert expert) {
        if (expert.id == null) {
            expert.id = db.nextExpertId();
        }
        table.put(expert.id, expert);
        return expert;
    }

    @Override
    public Expert findById(Long id) {
        return table.get(id);
    }

    @Override
    public Expert findByLogin(String login) {
        return table.values().stream()
                .filter(e -> e.login.equals(login))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Expert> findAll() {
        return new ArrayList<>(table.values());
    }

    @Override
    public boolean delete(Long id) {
        return table.remove(id) != null;
    }
}