package com.grantserver.dao.impl;

import com.grantserver.dao.EvaluationDAO;
import com.grantserver.model.Evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EvaluationDAOImpl implements EvaluationDAO {

    private final Map<Long, Evaluation> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public Evaluation save(Evaluation evaluation) {
        if (evaluation.id == null) {
            evaluation.id = idGenerator.incrementAndGet();
        }
        storage.put(evaluation.id, evaluation);
        return evaluation;
    }

    @Override
    public Evaluation findById(Long id) {
        return storage.get(id);
    }

    @Override
    public List<Evaluation> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Evaluation> findByExpertId(Long expertId) {
        return storage.values().stream()
                .filter(e -> e.expertId.equals(expertId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Evaluation> findByApplicationId(Long applicationId) {
        return storage.values().stream()
                .filter(e -> e.applicationId.equals(applicationId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(Long id) {
        return storage.remove(id) != null;
    }
}