package com.grantserver.dto.response;

import com.grantserver.model.GrantApplication;
import java.util.List;

public class GrantResultDTO {
    public List<WinnerDTO> winners;
    public Integer remainingFund;

    public GrantResultDTO() {}

    public GrantResultDTO(List<WinnerDTO> winners, Integer remainingFund) {
        this.winners = winners;
        this.remainingFund = remainingFund;
    }

    // Внутренний класс для победителя
    public static class WinnerDTO {
        public Long applicationId;
        public String title;
        public Double averageScore;
        public Integer givenAmount;

        public WinnerDTO(GrantApplication app, Double avg, Integer amount) {
            this.applicationId = app.id;
            this.title = app.title;
            this.averageScore = avg;
            this.givenAmount = amount;
        }
    }
}