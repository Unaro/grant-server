package com.grantserver.model;

public class Evaluation {
    public Long id;
    public GrantApplication application;
    public Double score;
    public Expert expert;

    public Evaluation() {}

    public Evaluation(Long id, GrantApplication application, Double score, Expert expert) {
        this.id = id;
        this.application = application;
        if (score < 0 || score > 5.0) {
            throw new IllegalArgumentException("Score must be between 0 and 5.0");
        }
        this.expert = expert;
        this.score = score;
    }
}