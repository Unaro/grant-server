package com.grantserver.service.algorithm;

public class DefaultRankingStrategy implements RankingStrategy {
    @Override
    public int compare(ScoredApplication a, ScoredApplication b) {
        // 1. Сравнение по баллу (чем больше, тем лучше -> desc)
        int scoreCompare = Double.compare(b.averageScore, a.averageScore);
        if (scoreCompare != 0) {
            return scoreCompare;
        }
        
        // 2. Если баллы равны, сравниваем по сумме (чем меньше просит, тем лучше -> asc)
        return Integer.compare(a.application.requestedSum, b.application.requestedSum);
    }
}