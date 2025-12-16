package com.grantserver.dao;

import com.grantserver.model.Evaluation;
import java.util.List;

public interface EvaluationDAO {
    Evaluation save(Evaluation evaluation);
    Evaluation findById(Long id);
    List<Evaluation> findAll();
    List<Evaluation> findByExpertId(Long expertId);
    List<Evaluation> findByApplicationId(Long applicationId);
    boolean delete(Long id);
}