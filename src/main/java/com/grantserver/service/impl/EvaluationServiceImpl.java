package com.grantserver.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.EvaluationDAO;
import com.grantserver.dao.ExpertDAO;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.dto.request.EvaluationCreateDTO;
import com.grantserver.dto.response.EvaluationDTO;
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
    public EvaluationDTO createEvaluation(EvaluationCreateDTO dto, Long expertId) {
        Expert expert = expertDAO.findById(expertId);
        
        GrantApplication application = grantApplicationDAO.findById(dto.applicationId);

        Evaluation evaluation = new Evaluation();

        evaluation.id = evaluationDAO.generateId(); 
        evaluation.application = application;
        evaluation.score = dto.score;

        expert.addEvaluation(evaluation);

        return new EvaluationDTO(evaluation);
    }
    
    @Override
    public EvaluationDTO updateEvaluation(Long id, EvaluationCreateDTO dto) {
        Evaluation existing = evaluationDAO.findById(id);
        if (existing == null) {
            throw new RuntimeException("Оценка не найдена");
        }
        
        existing.score = dto.score;

        return new EvaluationDTO(existing);
    }

    @Override
    public void deleteEvaluation(Long id) {
        if (!evaluationDAO.delete(id)) {
            throw new RuntimeException("Оценка не найдена для удаления");
        }
    }

    @Override
    public List<EvaluationDTO> getByExpert(Long expertId) {
        return evaluationDAO.findByExpertId(expertId).stream()
                .map(EvaluationDTO::new)
                .collect(Collectors.toList());
    }
}