package com.grantserver.service;

import java.util.List;

import com.grantserver.dto.request.EvaluationCreateDTO;
import com.grantserver.dto.response.EvaluationDTO;

public interface EvaluationService {
    EvaluationDTO updateEvaluation(Long id, EvaluationCreateDTO dto);
    void deleteEvaluation(Long id);
    List<EvaluationDTO> getByExpert(Long expertId);
    EvaluationDTO createEvaluation(EvaluationCreateDTO dto, Long expertId);
}