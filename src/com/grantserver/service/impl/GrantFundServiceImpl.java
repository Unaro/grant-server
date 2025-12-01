package com.grantserver.service.impl;

import com.grantserver.common.config.ServiceRegistry;
import com.grantserver.dao.EvaluationDAO;
import com.grantserver.dao.GrantApplicationDAO;
import com.grantserver.dto.request.GrantFundRequestDTO;
import com.grantserver.dto.response.GrantResultDTO;
import com.grantserver.model.Evaluation;
import com.grantserver.model.GrantApplication;
import com.grantserver.model.GrantApplicationStatus;
import com.grantserver.service.GrantFundService;
import com.grantserver.service.algorithm.DefaultRankingStrategy;
import com.grantserver.service.algorithm.RankingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GrantFundServiceImpl implements GrantFundService {

    private final GrantApplicationDAO applicationDAO;
    private final EvaluationDAO evaluationDAO;
    private final RankingStrategy rankingStrategy;

    public GrantFundServiceImpl() {
        ServiceRegistry registry = ServiceRegistry.getInstance();
        this.applicationDAO = registry.get(GrantApplicationDAO.class);
        this.evaluationDAO = registry.get(EvaluationDAO.class);
        // Стратегию можно тоже вынести в Registry, но пока создадим тут
        this.rankingStrategy = new DefaultRankingStrategy();
    }

    @Override
    public GrantResultDTO calculate(GrantFundRequestDTO request) {
        // 1. Получаем все АКТИВНЫЕ заявки
        List<GrantApplication> allApps = applicationDAO.findAll().stream()
                .filter(app -> app.status == GrantApplicationStatus.ACTIVE)
                .collect(Collectors.toList());

        // 2. Считаем средний балл и оборачиваем в ScoredApplication
        List<RankingStrategy.ScoredApplication> scoredApps = new ArrayList<>();
        
        for (GrantApplication app : allApps) {
            List<Evaluation> evals = evaluationDAO.findByApplicationId(app.id);
            double avg = 0.0;
            if (!evals.isEmpty()) {
                double sum = evals.stream().mapToInt(e -> e.score).sum();
                avg = sum / evals.size();
            }
            
            // Фильтр по порогу (Threshold)
            if (avg >= request.threshold) {
                scoredApps.add(new RankingStrategy.ScoredApplication(app, avg));
            }
        }

        // 3. Сортировка по стратегии
        scoredApps.sort(rankingStrategy::compare);

        // 4. Распределение фонда (Жадный алгоритм)
        List<GrantResultDTO.WinnerDTO> winners = new ArrayList<>();
        int currentFund = request.fund;

        for (RankingStrategy.ScoredApplication sa : scoredApps) {
            int requested = sa.application.requestedSum;
            
            if (currentFund >= requested) {
                // Денег хватает - выдаем грант
                currentFund -= requested;
                winners.add(new GrantResultDTO.WinnerDTO(sa.application, sa.averageScore, requested));
                
                // (Опционально) Можно менять статус заявки в БД на APPROVED
            }
            // Если денег не хватает, заявка пропускается (или можно реализовать частичное финансирование)
        }

        return new GrantResultDTO(winners, currentFund);
    }
}