package com.grantserver.dto.response;

import com.grantserver.model.Evaluation;

public class EvaluationDTO {
    public Long id;
    public Long applicationId;
    public Long expertId;
    public Double score;

    public EvaluationDTO() {}

    public EvaluationDTO(Evaluation evaluation) {
        this.id = evaluation.id;
        this.applicationId = evaluation.application.id;
        this.expertId = evaluation.id;
        this.score = evaluation.score;
    }
}