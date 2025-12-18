package com.grantserver.model;

import java.util.ArrayList;
import java.util.List;

public class Expert extends User {
    public String firstName;
    public String lastName;
    public List<String> fields; // Компетенции

    private final List<Evaluation> evaluations = new ArrayList<>();

    public Expert() {}

    public Expert(String login, String password, String firstName, String lastName) {
        super(login, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void addEvaluation(Evaluation evaluation) {
        this.evaluations.add(evaluation);
    }
    

    public void removeEvaluation(Long evaluationId) {
        this.evaluations.removeIf(e -> e.id.equals(evaluationId));
    }
}