package com.grantserver.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.EvaluationDAO;
import com.grantserver.dao.ExpertDAO;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.model.Evaluation;
import com.grantserver.model.Expert;
import com.grantserver.model.GrantApplication;
import com.grantserver.service.EvaluationService;

public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationDAO evaluationDAO;
    private final ExpertDAO expertDAO;
    private final GrantApplicationDAO grantApplicationDAO;

    public EvaluationServiceImpl() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        this.evaluationDAO = registry.get(EvaluationDAO.class);
        this.expertDAO = registry.get(ExpertDAO.class);
        this.grantApplicationDAO = registry.get(GrantApplicationDAO.class);
    }

    @Override
    public Evaluation createEvaluation(Long expertId, Long applicationId, Double score) {
        Expert expert = expertDAO.findById(expertId);
        
        GrantApplication application = grantApplicationDAO.findById(applicationId);

        Evaluation evaluation = new Evaluation();

        evaluation.id = evaluationDAO.generateId(); 
        evaluation.application = application;
        evaluation.score = score;

        expert.addEvaluation(evaluation);

        return evaluation;
    }
    
    @Override
    public Evaluation updateEvaluation(Long id, Double score) {
        Evaluation existing = evaluationDAO.findById(id);
        if (existing == null) {
            throw new RuntimeException("Evaluation not found");
        }
        
        existing.score = score;

        return existing;
    }

    @Override
    public void deleteEvaluation(Long id) {
        if (!evaluationDAO.delete(id)) {
            throw new RuntimeException("Evaluation not found for delete");
        }
    }

    @Override
    public List<Evaluation> getByExpert(Long expertId) {
        return evaluationDAO.findByExpertId(expertId).stream()
                .collect(Collectors.toList());
    }
}