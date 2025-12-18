package com.grantserver.service.algorithm;

import com.grantserver.model.GrantApplication;

public interface RankingStrategy {
    int compare(ScoredApplication a, ScoredApplication b);
    
    class ScoredApplication {
        public GrantApplication application;
        public Double averageScore;

        public ScoredApplication(GrantApplication application, Double averageScore) {
            this.application = application;
            this.averageScore = averageScore;
        }
    }
}