package com.grantserver.model;

public class Evaluation {
    public Long id;
    public GrantApplication application;
    public Double score;

    public Evaluation() {}

    public Evaluation(Long id, GrantApplication application, Double score) {
        this.id = id;
        this.application = application;
        this.score = score;
    }
}