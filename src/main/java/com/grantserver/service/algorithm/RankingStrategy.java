package com.grantserver.service.algorithm;

import com.grantserver.model.GrantApplication;

public interface RankingStrategy {
    // Метод сравнивает две заявки для сортировки
    int compare(ScoredApplication a, ScoredApplication b);
    
    // Вспомогательный класс-обертка
    class ScoredApplication {
        public GrantApplication application;
        public Double averageScore;

        public ScoredApplication(GrantApplication application, Double averageScore) {
            this.application = application;
            this.averageScore = averageScore;
        }
    }
}