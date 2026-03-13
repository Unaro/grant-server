package com.grantserver.model;

import java.util.List;

public class GrantResult {
    public List<Winner> winners;
    public Integer remainingFund;

    public GrantResult() {}

    public GrantResult(List<Winner> winners, Integer remainingFund) {
        this.winners = winners;
        this.remainingFund = remainingFund;
    }

    public static class Winner {
        public Long applicationId;
        public String title;
        public Double averageScore;
        public Integer givenAmount;

        public Winner(GrantApplication app, Double avg, Integer amount) {
            this.applicationId = app.id;
            this.title = app.title;
            this.averageScore = avg;
            this.givenAmount = amount;
        }
    }
}