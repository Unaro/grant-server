package com.grantserver.dao.impl;

import com.grantserver.common.db.Database;
import com.grantserver.dao.EvaluationDAO;
import com.grantserver.model.Evaluation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EvaluationDAOImpl implements EvaluationDAO {

    private final Map<Long, Evaluation> table;
    private final Database db;

    public EvaluationDAOImpl() {
        this.db = Database.getInstance();
        this.table = db.getEvaluationsTable();
    }

    @Override
    public Evaluation save(Evaluation evaluation) {
        if (evaluation.id == null) {
            evaluation.id = db.nextEvaluationId();
        }
        table.put(evaluation.id, evaluation);
        return evaluation;
    }

    @Override
    public Evaluation findById(Long id) {
        return table.get(id);
    }

    @Override
    public List<Evaluation> findAll() {
        return new ArrayList<>(table.values());
    }

    @Override
    public List<Evaluation> findByExpertId(Long expertId) {
        return table.values().stream()
                .filter(e -> e.expertId.equals(expertId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Evaluation> findByApplicationId(Long applicationId) {
        return table.values().stream()
                .filter(e -> e.applicationId.equals(applicationId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(Long id) {
        return table.remove(id) != null;
    }
}