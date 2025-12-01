package com.grantserver.service.impl;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.EvaluationDAO;
import com.grantserver.dao.ExpertDAO;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.dto.request.EvaluationCreateDTO;
import com.grantserver.dto.response.EvaluationDTO;
import com.grantserver.model.Evaluation;
import com.grantserver.service.EvaluationService;

import java.util.List;
import java.util.stream.Collectors;

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
    public EvaluationDTO addEvaluation(EvaluationCreateDTO dto) {
        // 1. Валидация существования эксперта и заявки
        if (expertDAO.findById(dto.expertId) == null) {
            throw new RuntimeException("Expert not found: " + dto.expertId);
        }
        if (grantApplicationDAO.findById(dto.applicationId) == null) {
            throw new RuntimeException("Application not found: " + dto.applicationId);
        }

        // 2. Создание оценки
        Evaluation evaluation = new Evaluation();
        evaluation.expertId = dto.expertId;
        evaluation.applicationId = dto.applicationId;
        evaluation.score = dto.score;

        Evaluation saved = evaluationDAO.save(evaluation);
        return new EvaluationDTO(saved);
    }

    @Override
    public EvaluationDTO updateEvaluation(Long id, EvaluationCreateDTO dto) {
        Evaluation existing = evaluationDAO.findById(id);
        if (existing == null) {
            throw new RuntimeException("Evaluation not found");
        }
        
        // Обновляем поля (оценку)
        existing.score = dto.score;
 
        Evaluation saved = evaluationDAO.save(existing);
        return new EvaluationDTO(saved);
    }

    @Override
    public void deleteEvaluation(Long id) {
        if (!evaluationDAO.delete(id)) {
            throw new RuntimeException("Evaluation not found for deletion");
        }
    }

    @Override
    public List<EvaluationDTO> getByExpert(Long expertId) {
        return evaluationDAO.findByExpertId(expertId).stream()
                .map(EvaluationDTO::new)
                .collect(Collectors.toList());
    }
}