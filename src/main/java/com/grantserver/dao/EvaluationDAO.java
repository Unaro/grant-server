package com.grantserver.dao;

import java.util.List;

import com.grantserver.model.Evaluation;

public interface EvaluationDAO {
    Evaluation findById(Long id);
    List<Evaluation> findAll();
    List<Evaluation> findByExpertId(Long expertId);
    List<Evaluation> findByApplicationId(Long applicationId);
    Long generateId();
    boolean delete(Long id);
}