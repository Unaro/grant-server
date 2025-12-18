package com.grantserver.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.grantserver.common.db.Database;
import com.grantserver.dao.EvaluationDAO;
import com.grantserver.model.Evaluation;
import com.grantserver.model.Expert;

public class EvaluationDAOImpl implements EvaluationDAO {

    private final Map<Long, Expert> table;
    private final Database db;

    public EvaluationDAOImpl() {
        this.db = Database.getInstance();
        this.table = db.getExpertsTable();
    }

    @Override
    public Evaluation findById(Long id) {
        for (Expert expert : table.values()) {
            for (Evaluation eval : expert.getEvaluations()) {
                if (eval.id.equals(id)) {
                    return eval;
                }
            }
        }
        return null;
    }

    @Override
    public List<Evaluation> findAll() {
        List<Evaluation> all = new ArrayList<>();
        for (Expert expert : table.values()) {
            all.addAll(expert.getEvaluations());
        }
        return all;
    }

    @Override
    public List<Evaluation> findByExpertId(Long expertId) {
        Expert expert = table.get(expertId);
        if (expert != null) {
            return expert.getEvaluations();
        }
        return new ArrayList<>();
    }

    @Override
    public List<Evaluation> findByApplicationId(Long applicationId) {
        List<Evaluation> result = new ArrayList<>();
        for (Expert expert : table.values()) {
            for (Evaluation eval : expert.getEvaluations()) {
                if (eval.application.id.equals(applicationId)) {
                    result.add(eval);
                }
            }
        }
        return result;
    }

    @Override
    public Long generateId() {
        return db.nextEvaluationId();
    }

    @Override
    public boolean delete(Long id) {
        for (Expert expert : table.values()) {
            boolean removed = expert.getEvaluations().removeIf(e -> e.id.equals(id));
            if (removed) return true;
        }
        return false;
    }
}