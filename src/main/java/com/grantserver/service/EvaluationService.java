package com.grantserver.service;

import java.util.List;

import com.grantserver.model.Evaluation;

public interface EvaluationService {
    Evaluation updateEvaluation(Long id, Double score);
    
    void deleteEvaluation(Long id);
    
    List<Evaluation> getByExpert(Long expertId);
    Evaluation createEvaluation(Long expertId, Long applicationId, Double score);
}