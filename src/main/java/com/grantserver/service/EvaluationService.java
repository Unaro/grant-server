package com.grantserver.service;

import com.grantserver.dto.request.EvaluationCreateDTO;
import com.grantserver.dto.response.EvaluationDTO;
import java.util.List;

public interface EvaluationService {
    EvaluationDTO addEvaluation(EvaluationCreateDTO dto);
    EvaluationDTO updateEvaluation(Long id, EvaluationCreateDTO dto);
    void deleteEvaluation(Long id);
    List<EvaluationDTO> getByExpert(Long expertId);
}